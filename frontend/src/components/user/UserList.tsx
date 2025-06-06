// components/UserList.tsx
import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { activeUser, inactiveUser } from '../../redux/slices/userSlice';
import { Link } from 'react-router-dom';
import Pagination from '../common/Pagination';
import userApi, { User } from '../../api/user.api';

const UserList: React.FC = () => {
  const dispatch = useDispatch();
  const [users, setUsers] = useState<User[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(1);
  const [limit] = useState(10);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [userStatusLoading, setUserStatusLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await userApi.getUsers({ page: page - 1, limit, search: searchTerm });
      setUsers(response.data.users);
      setTotal(response.data.totalItems);
    } catch (err) {
      setError('Failed to fetch users');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page, searchTerm]);

  const handleStatus = async (id: number, status: string) => {
    const action = status === 'Active' ? inactiveUser : activeUser;
    if (window.confirm(`Are you sure you want to ${status === 'Active' ? 'deactivate' : 'activate'} this user?`)) {
      setUserStatusLoading(true);
      try {
        await dispatch(action(id.toString()) as any);
        fetchUsers();
      } catch (err) {
        console.error('Error updating user status', err);
      } finally {
        setUserStatusLoading(false);
      }
    }
  };

  if (loading) return <div className="text-center py-10">Loading...</div>;
  if (error) return <div className="text-red-500 text-center">{error}</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">User Management</h1>
        <Link
          to="/users/add"
          className="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark"
        >
          Add New User
        </Link>
      </div>

      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="mb-4 w-full md:w-64 px-4 py-2 border rounded"
        placeholder="Search users..."
      />

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {users.map((user) => (
              <tr key={user.id}>
                <td className="px-6 py-4 whitespace-nowrap">{user.name}</td>
                <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                <td className="px-6 py-4 whitespace-nowrap">{user.role}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${user.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                    {user.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <Link to={`/users/edit/${user.id}`} className="text-primary hover:text-primary-dark mr-4">
                    Edit
                  </Link>
                  <button
                    onClick={() => handleStatus(user.id, user.status || 'Inactive')}
                    disabled={userStatusLoading}
                    className={`${
                      user.status === 'Active' ? 'text-red-600 hover:text-red-900' : 'text-green-600 hover:text-green-900'
                    } disabled:opacity-50`}
                  >
                    {user.status === 'Active' ? 'Deactivate' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        currentPage={page}
        totalPages={Math.ceil(total / limit)}
        onPageChange={(newPage) => setPage(newPage)}
      />
    </div>
  );
};

export default UserList;

import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../redux/store';
import { fetchUsers, deleteUser } from '../../redux/slices/userSlice';
import { Link } from 'react-router-dom';
import Pagination from '../common/Pagination';
import { User } from '../../api/user.api';

let renderTimes = 0;

const UserList: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { users, loading, error, total, page, limit } = useSelector((state: RootState) => state.users);
  const [searchTerm, setSearchTerm] = useState('');
  const [isDeleting, setIsDeleting] = useState(false);

  console.log("renderTimes", renderTimes);
  renderTimes++;
  console.log({ users, loading, error, total, page, limit } );
  useEffect(() => {
    dispatch(fetchUsers({ page, limit, search: searchTerm }));
  }, [dispatch, page, limit, searchTerm]);

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const handlePageChange = (newPage: number) => {
    dispatch(fetchUsers({ page: newPage, limit, search: searchTerm }));
  };

  const handleStatus = async (id: number) => {
    if (window.confirm('Are you sure you want to Inactive this user?')) {
      setIsDeleting(true);
      try {
        await dispatch(deleteUser(id.toString()));
        // Refresh the current page if it's the last item
        if (users.length === 1 && page > 0) {
          dispatch(fetchUsers({ page: page - 1, limit, search: searchTerm }));
        }
      } catch (error) {
        console.error('Error deleting user:', error);
      } finally {
        setIsDeleting(false);
      }
    }
  };

  if (loading) return <div className="flex justify-center items-center h-64">Loading...</div>;
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

      <div className="mb-4">
        <input
          type="text"
          placeholder="Search users..."
          className="w-full md:w-64 px-4 py-2 border rounded"
          value={searchTerm}
          onChange={handleSearch}
        />
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Name
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Email
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Role
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {users.map((user: User) =>{ 
              return(
              <tr key={user.id}>
                <td className="px-6 py-4 whitespace-nowrap">{user.name}</td>
                <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                    user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'
                  }`}>
                    {user.role}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                    user.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {user.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <Link
                    to={`/users/edit/${user.id}`}
                    className="text-primary hover:text-primary-dark mr-4"
                  >
                    Edit
                  </Link>
                  <button
                    onClick={() => handleStatus(user.id)}
                    disabled={isDeleting}
                    className={`${
                      user.status === 'Active'
                        ? 'text-red-600 hover:text-red-900'
                        : 'text-green-600 hover:text-green-900'
                    } disabled:opacity-50`}
                  >
                    {user.status === 'Active' ? 'Inactive' : 'Active'}
                  </button>
                </td>
              </tr>
            )})}
          </tbody>
        </table>
      </div>

      <div className="mt-4">
        <Pagination
          currentPage={page}
          totalPages={Math.ceil(total / limit)}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
};

export default UserList; 
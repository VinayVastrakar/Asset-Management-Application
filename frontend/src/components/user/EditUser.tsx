import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import { RootState, AppDispatch } from '../../redux/store';
import { fetchUserById, updateUser } from '../../redux/slices/userSlice';

interface UserFormData {
  name: string;
  email: string;
  mobileNo: string;
  role: string;
  status: string;
}

const EditUser: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { currentUser, fetchLoading, updateLoading, error } = useSelector((state: RootState) => state.users);

  const [formData, setFormData] = useState<UserFormData>({
    name: '',
    email: '',
    mobileNo: '',
    role: '',
    status: 'Active',
  });

  useEffect(() => {
    if (id) {
      dispatch(fetchUserById(id));
    }
  }, [dispatch, id]);
  

  useEffect(() => {
    if (currentUser) {
      setFormData({
        name: currentUser.name,
        email: currentUser.email,
        mobileNo: currentUser.mobileNumber,
        role: currentUser.role,
        status: currentUser.status || 'Active',
      });
    }
  }, [currentUser]);

  
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    try {
      await dispatch(updateUser({ id, userData: formData })).unwrap();
      navigate('/users');
    } catch (err) {
      console.error('Error updating user:', err);
    }
  };

  if (fetchLoading) return <div className="flex justify-center items-center h-64">Loading...</div>;
  if (error) return <div className="text-red-500 text-center">{error}</div>;
  if (!currentUser) return <div className="text-center">User not found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-2xl font-bold mb-6">Edit User</h1>

        <form onSubmit={handleSubmit} className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
          {[
            { label: 'Name', name: 'name', type: 'text' },
            { label: 'Email', name: 'email', type: 'email' },
            { label: 'Mobile No', name: 'mobileNo', type: 'text' },
          ].map(({ label, name, type }) => (
            <div className="mb-4" key={name}>
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor={name}>
                {label}
              </label>
              <input
                type={type}
                id={name}
                name={name}
                value={formData[name as keyof UserFormData]}
                onChange={handleChange}
                readOnly={name === 'name' || name === 'email'}
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${
                  name === 'name' || name === 'email' ? 'bg-gray-100 cursor-not-allowed' : ''
                }`}
                required
              />
            </div>
          ))}

          <div className="mb-4">
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="role">
              Role
            </label>
            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleChange}
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <div className="mb-6">
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="status">
              Status
            </label>
            <select
              id="status"
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            >
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
            </select>
          </div>

          <div className="flex items-center justify-between">
          <button
              type="submit"
              disabled={updateLoading}
              className="bg-primary text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline hover:bg-primary-dark disabled:opacity-50"
            >
              {updateLoading ? 'Saving...' : 'Save Changes'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/users')}
              className="text-gray-600 hover:text-gray-800"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditUser;

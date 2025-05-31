import React, { useCallback, useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../redux/store';
import { fetchAssetById } from '../../redux/slices/assetSlice';
import axios from 'axios';
import api from 'api/config';
import { assetApi } from 'api/asset.api';
import userApi from 'api/user.api';

const AssetView: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const { currentAsset: asset, loading, error } = useSelector((state: RootState) => state.assets);

  const [isModalOpen, setModalOpen] = useState(false);
  const [users, setUsers] = useState<{ id: number; name: string }[]>([]);
  const [selectedUser, setSelectedUser] = useState<number | null>(null);
  const [assigning, setAssigning] = useState(false);

  useEffect(() => {
    if (id) {
      dispatch(fetchAssetById(parseInt(id)));
    }
  }, [dispatch, id]);

  const openModal = async () => {
    setModalOpen(true);
    try {
      const res = await userApi.getActiveUsers();
      setUsers(res.data.users);
    } catch (err) {
      console.error('Failed to load users', err);
    }
  };

  const returnAsset = async (id: number) => {
    const confirmed = window.confirm('Are you sure you want to return this asset?');
    if (!confirmed) return;
  
    try {
      // Optionally show a loading state
      setAssigning(true);
  
      await assetApi.returnAsset(id);
  
      // Optionally show success message
      alert('Asset successfully returned.');
  
      // Refresh asset details after returning
      dispatch(fetchAssetById(id));
    } catch (err) {
      console.error('Failed to return asset', err);
      alert('Failed to return the asset. Please try again.');
    } finally {
      setAssigning(false);
    }
  };

  const assignUser = async () => {
    if (!selectedUser || !asset?.id) return;
    setAssigning(true);
    try {
      await assetApi.assignAssetToUser(asset.id, selectedUser);
      setModalOpen(false);
      dispatch(fetchAssetById(asset.id)); // refresh asset view
    } catch (err) {
      console.error('Failed to assign user', err);
    } finally {
      setAssigning(false);
    }
  };

  const renderStatusBadge = useCallback((status: string) => {
    const statusStyles: Record<string, string> = {
      AVAILABLE: 'bg-green-100 text-green-800',
      ASSIGNED: 'bg-blue-100 text-blue-800',
      INACTIVE: 'bg-red-100 text-red-800',
    };
    return (
      <span
        className={`px-2 inline-flex text-xs font-semibold rounded-full ${
          statusStyles[status] || 'bg-gray-100 text-gray-800'
        }`}
      >
        {status}
      </span>
    );
  }, []);

  if (loading) return <div className="flex justify-center items-center h-64">Loading...</div>;
  if (error) return <div className="text-red-600 p-4">{error}</div>;
  if (!asset) return <div className="text-gray-600 p-4">Asset not found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-semibold text-gray-800">{asset.name}</h1>
          <div className="space-x-2 flex items-center">
            {renderStatusBadge(asset.status)}

            {asset.status === 'AVAILABLE' && (
              <button
                onClick={openModal}
                className="px-4 py-2 bg-blue-100 text-blue-600 rounded hover:bg-blue-200 transition"
              >
                Assign User
              </button>
            )}

            {asset.status === 'ASSIGNED' && (
              <button
                onClick={()=> returnAsset(asset.id)}
                className="px-4 py-2 bg-green-100 text-green-600 rounded hover:bg-green-200 transition"
              >
                Return Asset
              </button>
            )}

            <button
              onClick={() => navigate(`/assets/edit/${asset.id}`)}
              className="px-4 py-2 bg-yellow-100 text-yellow-600 rounded hover:bg-yellow-200 transition"
            >
              Edit Asset
            </button>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          {asset.imageUrl && (
            <div className="h-96 overflow-hidden">
              <img
                src={asset.imageUrl}
                alt={asset.name}
                className="w-full h-full object-contain"
              />
            </div>
          )}

          <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h2 className="text-xl font-semibold mb-4">Asset Details</h2>
              <p><strong>Description:</strong> {asset.description}</p>
              <p><strong>Status:</strong> {asset.status}</p>
              <p><strong>Category:</strong> {asset.categoryName}</p>
            </div>
            <div>
              <h2 className="text-xl font-semibold mb-4">Dates & Warranty</h2>
              <p><strong>Purchase Date:</strong> {new Date(asset.purchaseDate).toLocaleDateString()}</p>
              <p><strong>Expiry Date:</strong> {new Date(asset.expiryDate).toLocaleDateString()}</p>
              <p><strong>Warranty:</strong> {asset.warrantyPeriod} months</p>
            </div>
          </div>
        </div>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-semibold mb-4">Assign User</h2>
            <select
              value={selectedUser || ''}
              onChange={(e) => setSelectedUser(Number(e.target.value))}
              className="w-full border border-gray-300 rounded p-2 mb-4"
            >
              <option value="">Select a user</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.name}
                </option>
              ))}
            </select>

            <div className="flex justify-end space-x-2">
              <button
                onClick={() => setModalOpen(false)}
                className="px-4 py-2 bg-gray-100 text-gray-600 rounded hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={assignUser}
                disabled={assigning || !selectedUser}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                {assigning ? 'Assigning...' : 'Assign'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AssetView;

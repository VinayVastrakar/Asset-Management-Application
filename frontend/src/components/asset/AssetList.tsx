import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../redux/store';
import { fetchAssets, deleteAsset, Asset } from '../../redux/slices/assetSlice';
import { Link, useNavigate } from 'react-router-dom';

const AssetList: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { assets, loading, error } = useSelector((state: RootState) => state.assets);

  useEffect(() => {
    dispatch(fetchAssets({ page: 0, limit: 10 }));
  }, [dispatch]);

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this asset?')) {
      try {
        await dispatch(deleteAsset(id)).unwrap();
      } catch (err) {
        console.error('Failed to delete asset:', err);
      }
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64">Loading...</div>;
  }

  if (error) {
    return <div className="text-red-600 p-4">{error}</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Asset Management</h1>
        <Link
          to="/assets/add"
          className="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark"
        >
          Add New Asset
        </Link>
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Image</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Asset Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Assigned User</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Purchase Date</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Expiry Date</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {assets.map((asset: Asset) => (
              <tr key={asset.id}>
                <td className="px-6 py-4 whitespace-nowrap">
                  {asset.imageUrl ? (
                    <img src={asset.imageUrl} alt={asset.name} className="h-14 w-12 object-cover rounded" />
                  ) : (
                    <span className="text-gray-400">No Image</span>
                  )}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">{asset.name}</td>
                <td className="px-6 py-4 whitespace-nowrap">{asset.assignedToUserName}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                    asset.status === 'AVAILABLE'
                    ? 'bg-green-100 text-green-800'
                    : asset.status === 'ASSIGNED'
                    ? 'bg-blue-100 text-blue-800'
                    : 'bg-red-100 text-red-800'
                  }`}>
                    {asset.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {new Date(asset.purchaseDate).toLocaleDateString()}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {new Date(asset.expiryDate).toLocaleDateString()}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                  <button
                    onClick={() => navigate(`/assets/${asset.id}`)}
                    className="text-blue-600 hover:text-blue-900"
                  >
                    View
                  </button>
                  <button
                    onClick={() => navigate(`/assets/edit/${asset.id}`)}
                    className="text-yellow-600 hover:text-yellow-900"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(asset.id)}
                    className="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AssetList;

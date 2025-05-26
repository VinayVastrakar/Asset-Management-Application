import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../redux/store';
import { fetchAssetById, deleteAsset } from '../../redux/slices/assetSlice';

const AssetView: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const { currentAsset: asset, loading, error } = useSelector((state: RootState) => state.assets);

  useEffect(() => {
    if (id) {
      const response = dispatch(fetchAssetById(parseInt(id)));
    }
  }, [dispatch, id]);

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this asset?')) {
      try {
        await dispatch(deleteAsset(parseInt(id!))).unwrap();
        navigate('/assets');
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

  if (!asset) {
    return <div className="text-gray-600 p-4">Asset not found</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-semibold text-gray-800">{asset.name}</h1>
          <div className="space-x-2">
            <button
              onClick={() => navigate(`/assets/edit/${asset.id}`)}
              className="px-4 py-2 bg-yellow-100 text-yellow-600 rounded hover:bg-yellow-200"
            >
              Edit Asset
            </button>
            <button
              onClick={handleDelete}
              className="px-4 py-2 bg-red-100 text-red-600 rounded hover:bg-red-200"
            >
              Delete Asset
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

          <div className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h2 className="text-xl font-semibold mb-4">Asset Details</h2>
                <div className="space-y-3">
                  <div>
                    <label className="text-sm text-gray-500">Description</label>
                    <p className="text-gray-800">{asset.description}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Status</label>
                    <p className="text-gray-800">{asset.status}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Category Name</label>
                    <p className="text-gray-800">{asset.categoryName}</p>
                  </div>
                </div>
              </div>

              <div>
                <h2 className="text-xl font-semibold mb-4">Dates & Warranty</h2>
                <div className="space-y-3">
                  <div>
                    <label className="text-sm text-gray-500">Purchase Date</label>
                    <p className="text-gray-800">{new Date(asset.purchaseDate).toLocaleDateString()}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Expiry Date</label>
                    <p className="text-gray-800">{new Date(asset.expiryDate).toLocaleDateString()}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Warranty Period</label>
                    <p className="text-gray-800">{asset.warrantyPeriod} months</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssetView; 
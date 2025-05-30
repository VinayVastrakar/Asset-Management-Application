import React, { useEffect, useCallback, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../redux/store';
import {
  fetchAssets,
  Asset,
  inactiveAsset,
  activeAsset,
} from '../../redux/slices/assetSlice';
import { Link, useNavigate } from 'react-router-dom';

const AssetList: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

  const { assets, loading, error, isLoaded } = useSelector((state: RootState) => state.assets);

  const assetList = useMemo(() => Object.values(assets), [assets]);

  const headers = useMemo(
    () => ['Image', 'Asset Name', 'Assigned User', 'Status', 'Purchase Date', 'Expiry Date', 'Actions'],
    []
  );

  useEffect(() => {
    if (!isLoaded) {  // <-- Only fetch if not loaded
      dispatch(fetchAssets({ page: 0, limit: 10 }));
    }
  }, [dispatch, isLoaded]);

  const handleStatusToggle = useCallback(
    async (id: number, status: string) => {
      const isInactive = status === 'INACTIVE';
      const action = isInactive ? activeAsset : inactiveAsset;
      const verb = isInactive ? 'activate' : 'inactivate';

      if (window.confirm(`Are you sure you want to ${verb} this asset?`)) {
        try {
          await dispatch(action(id)).unwrap();
          dispatch(fetchAssets({ page: 0, limit: 10 }));
        } catch (err) {
          console.error(`Failed to ${verb} asset:`, err);
        }
      }
    },
    [dispatch]
  );

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

  if (loading) {
    return <div className="flex justify-center items-center h-64">Loading...</div>;
  }

  if (error) {
    return <div className="text-red-600 p-4">Error: {error}</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Asset Management</h1>
        <Link to="/assets/add" className="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark transition">
          Add New Asset
        </Link>
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {headers.map((header) => (
                <th key={header} className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  {header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {assetList.length === 0 ? (
              <tr>
                <td colSpan={headers.length} className="px-6 py-4 text-center text-gray-500">
                  No assets found.
                </td>
              </tr>
            ) : (
              assetList.map((asset) => {
                const toggleLabel =
                  asset.status === 'INACTIVE'
                    ? 'Activate'
                    : asset.status === 'AVAILABLE'
                    ? 'Inactivate'
                    : null;

                const toggleClass =
                  asset.status === 'INACTIVE'
                    ? 'text-green-600 hover:text-green-900'
                    : 'text-red-600 hover:text-red-900';

                return (
                  <tr key={asset.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {asset.imageUrl ? (
                        <img src={asset.imageUrl} alt={asset.name} className="h-14 w-12 object-cover rounded" />
                      ) : (
                        <span className="text-gray-400 text-sm">No Image</span>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">{asset.name}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {asset.assignedToUserName || <span className="text-gray-500">Unassigned</span>}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">{renderStatusBadge(asset.status)}</td>
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
                      {toggleLabel && (
                        <button
                          onClick={() => handleStatusToggle(asset.id, asset.status)}
                          className={toggleClass}
                        >
                          {toggleLabel}
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AssetList;

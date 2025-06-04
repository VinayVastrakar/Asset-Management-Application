import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { assetApi } from '../../api/asset.api';

interface Asset {
  id: number;
  name: string;
  description: string;
  categoryId: number;
  categoryName: string;
  purchaseDate: string;
  expiryDate: string;
  warrantyPeriod: number;
  assignedToUserName: string;
  status: string;
  imageUrl?: string;
} 

const AssetList: React.FC = () => {
  const [assets, setAssets] = useState<Asset[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await assetApi.getAssets({ page: 0, limit: 10 });
      setAssets(response.data.data); // assuming `data` is in `response.data.data`
    } catch (err: any) {
      setError(err.message || 'Failed to fetch assets');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleStatusToggle = useCallback(
    async (id: number, status: string) => {
      const isInactive = status === 'INACTIVE';
      const verb = isInactive ? 'activate' : 'inactivate';
  
      if (window.confirm(`Are you sure you want to ${verb} this asset?`)) {
        try {
          if (isInactive) {
            await assetApi.activeAsset(id);
          } else {
            await assetApi.inactiveAsset(id);
          }
          await fetchData(); // Refresh list
        } catch (err) {
          console.error(`Failed to ${verb} asset:`, err);
        }
      }
    },
    [fetchData]
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

  const headers = useMemo(
    () => ['Image', 'Asset Name', 'Assigned User', 'Status', 'Purchase Date', 'Expiry Date', 'Actions'],
    []
  );

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
            {assets.length === 0 ? (
              <tr>
                <td colSpan={headers.length} className="px-6 py-4 text-center text-gray-500">
                  No assets found.
                </td>
              </tr>
            ) : (
              assets.map((asset) => {
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

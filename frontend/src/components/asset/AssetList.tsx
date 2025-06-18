import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { assetApi } from '../../api/asset.api';
import { Category } from 'api/category.api';
import { categoryApi } from '../../api/category.api';

interface Asset {
  id: number;
  name: string;
  description: string;
  categoryId: number;
  categoryName: string;
  warrantyPeriod: number;
  assignedToUserName: string;
  status: string;
  imageUrl?: string;
}

const AssetList: React.FC = () => {
  const [assets, setAssets] = useState<Asset[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [limit] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [categoryId, setCategoryId] = useState<number | undefined>();
  const [status, setStatus] = useState<string | undefined>();
  const [categories, setCategories] = useState<Category[]>([]);
  const navigate = useNavigate();

  const fetchCategories = useCallback(async () => {
    try {
      const response = await categoryApi.getCategories();
      setCategories(response);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch categories');
    }
  }, []);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await assetApi.getAssets({ page, limit, categoryId, status });
      setAssets(response.data.data);
      setTotalItems(response.data.total);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch assets');
    } finally {
      setLoading(false);
    }
  }, [page, limit, categoryId, status]);

  useEffect(() => {
    fetchCategories();
  }, [fetchCategories]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleStatusToggle = async (id: number, currentStatus: string) => {
    const isInactive = currentStatus === 'INACTIVE';
    const verb = isInactive ? 'activate' : 'inactivate';

    if (window.confirm(`Are you sure you want to ${verb} this asset?`)) {
      try {
        isInactive ? await assetApi.activeAsset(id) : await assetApi.inactiveAsset(id);
        fetchData();
      } catch (err) {
        console.error(`Failed to ${verb} asset:`, err);
      }
    }
  };

  const renderStatusBadge = (status: string) => {
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
  };

  const headers = useMemo(
    () => ['Image', 'Asset Name', 'Category', 'Assigned User', 'Status', 'Actions'],
    []
  );

  const totalPages = Math.ceil(totalItems / limit);

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Asset Management</h1>
        <Link to="/assets/add" className="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark transition">
          Add New Asset
        </Link>
      </div>

      {/* Filters */}
      <div className="mb-4 flex space-x-4">
        <select
          value={categoryId || ''}
          onChange={(e) => setCategoryId(e.target.value ? Number(e.target.value) : undefined)}
          className="border rounded px-3 py-2"
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>

        <select
          value={status || ''}
          onChange={(e) => setStatus(e.target.value || undefined)}
          className="border rounded px-3 py-2"
        >
          <option value="">All Statuses</option>
          <option value="AVAILABLE">Available</option>
          <option value="ASSIGNED">Assigned</option>
          <option value="INACTIVE">Inactive</option>
        </select>

        <button onClick={() => fetchData()} className="bg-blue-500 text-white px-4 py-2 rounded">
          Apply Filters
        </button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">Loading...</div>
      ) : error ? (
        <div className="text-red-600 p-4">Error: {error}</div>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                {headers.map((header) => (
                  <th
                    key={header}
                    className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                  >
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
                      <td className="px-6 py-4 whitespace-nowrap">{asset.categoryName}</td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {asset.assignedToUserName || <span className="text-gray-500">Unassigned</span>}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">{renderStatusBadge(asset.status)}</td>
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
      )}

      {/* Pagination */}
      <div className="flex justify-end mt-4 space-x-2">
        <button
          disabled={page === 0}
          onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          Prev
        </button>
        <span className="px-3 py-1">Page {page + 1} of {totalPages}</span>
        <button
          disabled={page + 1 >= totalPages}
          onClick={() => setPage((prev) => prev + 1)}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default AssetList;

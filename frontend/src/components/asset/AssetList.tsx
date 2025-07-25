import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { assetApi } from '../../api/asset.api';
import { Category } from 'api/category.api';
import { categoryApi } from '../../api/category.api';
import { saveAs } from 'file-saver';

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
  const [exportLoading, setExportLoading] = useState(false);
  const navigate = useNavigate();
  const [isAnimating, setIsAnimating] = useState(false);
  const [pageDirection, setPageDirection] = useState<'next' | 'prev'>('next');

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

  const handleExport = async () => {
    setExportLoading(true);
    try {
      const blob = await assetApi.exportAssetAssignmentHistory(undefined, categoryId);
      const fileName = `asset_assignment_history_${new Date().toISOString().split('T')[0]}.xlsx`;
      saveAs(blob, fileName);
    } catch (err: any) {
      console.error('Export failed:', err);
      alert('Failed to export data. Please try again.');
    } finally {
      setExportLoading(false);
    }
  };

  const getExportTooltip = () => {
    if (categoryId) {
      const selectedCategory = categories.find(cat => cat.id === categoryId);
      return `Export assignment history for ${selectedCategory?.name || 'selected category'}`;
    }
    return 'Export all asset assignment history';
  };

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
      STOLEN: 'bg-red-700 text-white', // Add a strong red for stolen
      DISPOSED: 'bg-gray-500 text-white', // Add a gray for disposed
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

  const handlePageChange = (newPage: number) => {
    setPageDirection(newPage > page ? 'next' : 'prev');
    setIsAnimating(true);
    setTimeout(() => {
      setPage(newPage);
      setIsAnimating(false);
    }, 300); // Match the transition duration
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Asset Management</h1>
        <div className="flex space-x-3">
          <button
            onClick={handleExport}
            disabled={exportLoading}
            title={getExportTooltip()}
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition disabled:opacity-50 flex items-center space-x-2 relative group"
          >
            {exportLoading ? (
              <>
                <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                <span>Exporting...</span>
              </>
            ) : (
              <>
                <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <span>Export Assignment History</span>
              </>
            )}
            {/* Tooltip */}
            <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap pointer-events-none">
              {getExportTooltip()}
              <div className="absolute top-full left-1/2 transform -translate-x-1/2 border-4 border-transparent border-t-gray-800"></div>
            </div>
          </button>
          <Link to="/assets/add" className="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark transition">
            Add New Asset
          </Link>
        </div>
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
          <option value="STOLEN">Stolen</option>
          <option value="DISPOSED">Disposed</option>
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
            <tbody
              className={`bg-white divide-y divide-gray-200 transition-all duration-300 ${
                isAnimating
                  ? pageDirection === 'next'
                    ? 'opacity-0 -translate-y-4 pointer-events-none'
                    : 'opacity-0 translate-y-4 pointer-events-none'
                  : 'opacity-100 translate-y-0'
              }`}
            >
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
          disabled={page === 0 || isAnimating}
          onClick={() => handlePageChange(Math.max(page - 1, 0))}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          Prev
        </button>
        <span className="px-3 py-1">Page {page + 1} of {totalPages}</span>
        <button
          disabled={page + 1 >= totalPages || isAnimating}
          onClick={() => handlePageChange(page + 1)}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default AssetList;

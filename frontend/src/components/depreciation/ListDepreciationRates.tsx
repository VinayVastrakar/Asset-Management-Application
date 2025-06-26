import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { depreciationRateApi, DepreciationRate, DepreciationRateQueryParams, PaginatedResponse } from '../../api/depreciationRate.api';
import { categoryApi, Category } from '../../api/category.api';

const PAGE_SIZE = 10;

const ListDepreciationRates: React.FC = () => {
  const navigate = useNavigate();
  const [rates, setRates] = useState<DepreciationRate[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState({ category: '', financialYear: '' });
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [total, setTotal] = useState(0);
  const [categories, setCategories] = useState<{ id: number; name: string }[]>([]);
  const [financialYears, setFinancialYears] = useState<string[]>([]);

  useEffect(() => {
    // Fetch categories on mount
    const fetchCategories = async () => {
      try {
        const data = await categoryApi.getCategories();
        setCategories(data || []);
      } catch (err) {
        setCategories([]);
      }
    };
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchRates();
    // eslint-disable-next-line
  }, [page, filter.category, filter.financialYear]);

  const fetchRates = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: DepreciationRateQueryParams = {
        page: page - 1,
        limit: PAGE_SIZE,
        categoryId: filter.category ? Number(filter.category) : undefined,
        financialYear: filter.financialYear || undefined,
      };
      const response = await depreciationRateApi.getDepreciationRates(params);
      const paginated = response.data; // ApiResponse<PaginatedResponse<DepreciationRate>>
      console.log(response);
      setRates(paginated.data);
      setTotalPages(Math.ceil(paginated.total / paginated.limit));
      setTotal(paginated.total);
      // Extract unique financial years from the rates
      const years = Array.from(new Set(paginated.data.map((r: DepreciationRate) => r.financialYear)));
      setFinancialYears(years);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch depreciation rates');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this rate?')) return;
    try {
      await depreciationRateApi.deleteDepreciationRate(id);
      fetchRates();
    } catch (err: any) {
      setError(err.message || 'Failed to delete depreciation rate');
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-semibold text-gray-800">Depreciation Rates</h1>
          <button
            onClick={() => navigate('/depreciation-rates/add')}
            className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark"
          >
            Add Rate
          </button>
        </div>
        <div className="flex gap-4 mb-4">
          <select
            value={filter.category}
            onChange={e => setFilter(f => ({ ...f, category: e.target.value }))}
            className="px-3 py-2 border border-gray-300 rounded text-sm"
          >
            <option value="">All Categories</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
          <select
            value={filter.financialYear}
            onChange={e => setFilter(f => ({ ...f, financialYear: e.target.value }))}
            className="px-3 py-2 border border-gray-300 rounded text-sm"
          >
            <option value="">All Financial Years</option>
            {financialYears.map(fy => (
              <option key={fy} value={fy}>{fy}</option>
            ))}
          </select>
        </div>
        {error && <div className="text-red-600 mb-4">{error}</div>}
        {loading ? (
          <div>Loading...</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Category</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Asset Type</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Financial Year</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Depreciation %</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Method</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Useful Life (yrs)</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Residual %</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Effective From</th>
                  <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Effective To</th>
                  <th className="px-4 py-2 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {rates.length === 0 ? (
                  <tr>
                    <td colSpan={10} className="text-center py-4 text-gray-500">No rates found</td>
                  </tr>
                ) : (
                  rates.map(rate => (
                    <tr key={rate.id}>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.categoryName}</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.assetType || '-'}</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.financialYear}</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.depreciationPercentage}%</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.depreciationMethod}</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.usefulLifeYears || '-'}</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.residualValuePercentage || '-'}</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.effectiveFromDate}</td>
                      <td className="px-4 py-2 whitespace-nowrap">{rate.effectiveToDate || '-'}</td>
                      <td className="px-4 py-2 whitespace-nowrap text-right">
                        <button
                          onClick={() => navigate(`/depreciation-rates/edit/${rate.id}`)}
                          className="text-yellow-600 hover:text-yellow-900 mr-4"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDelete(rate.id)}
                          className="text-red-600 hover:text-red-900"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
            {/* Pagination Controls */}
            <div className="flex justify-between items-center mt-4">
              <div className="text-sm text-gray-600">
                Showing {rates.length} of {total} rates
              </div>
              <div className="space-x-2">
                <button
                  onClick={() => setPage(p => Math.max(1, p - 1))}
                  disabled={page === 1}
                  className="px-3 py-1 border rounded disabled:opacity-50"
                >
                  Previous
                </button>
                <span className="text-sm">Page {page} of {totalPages}</span>
                <button
                  onClick={() => setPage(p => Math.min(totalPages, p + 1))}
                  disabled={page === totalPages}
                  className="px-3 py-1 border rounded disabled:opacity-50"
                >
                  Next
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ListDepreciationRates; 
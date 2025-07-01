import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { purchaseHistoryApi, PurchaseHistory } from '../../api/purchaseHistory.api';
import { assetApi } from '../../api/asset.api';
import Pagination from '../common/Pagination';

const ListPurchaseHistory: React.FC = () => {
  const navigate = useNavigate();
  const [purchaseHistories, setPurchaseHistories] = useState<PurchaseHistory[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [limit] = useState(10);
  const [total, setTotal] = useState(1);
  const [assetId, setAssetId] = useState<number | undefined>();
  const [status, setStatus] = useState<string | undefined>();
  const [assets, setAssets] = useState<{ id: number; name: string }[]>([]);

  const fetchAssets = useCallback(async () => {
    try {
      const response = await assetApi.getAssets({ page: page-1, limit: 1000 });
      setAssets(response.data.data.map(asset => ({ id: asset.id, name: asset.name })));
    } catch (err: any) {
      setError(err.message || 'Failed to fetch assets');
    }
  }, []);

  const fetchData = useCallback(async () => { 
    setLoading(true);
    setError(null);
    try {
      const response = await purchaseHistoryApi.getPurchaseHistories({ page:page-1, limit, assetId});
      setPurchaseHistories(response.content);
      setTotal(response.size);
      console.log(response);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch purchase histories');
    } finally {
      setLoading(false);
    }
  }, [page, limit, assetId]);

  useEffect(() => {
    fetchAssets();
  }, [fetchAssets]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // const handleStatusToggle = async (id: number, currentStatus: string) => {
  //   const isInactive = currentStatus === 'INACTIVE';
  //   const verb = isInactive ? 'activate' : 'inactivate';

  //   if (window.confirm(`Are you sure you want to ${verb} this purchase history?`)) {
  //     try {
  //       await purchaseHistoryApi.updatePurchaseHistory(id, { status: isInactive ? 'ACTIVE' : 'INACTIVE' });
  //       fetchData();
  //     } catch (err) {
  //       console.error(`Failed to ${verb} purchase history:`, err);
  //     }
  //   }
  // };

  if (loading && purchaseHistories.length === 0) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-semibold text-gray-800">Purchase History</h1>
          <button
            onClick={() => navigate('/purchase-history/add')}
            className="bg-primary text-white px-4 py-2 text-sm font-medium rounded hover:bg-primary-dark"
          >
            Add New Purchase
          </button>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 text-sm">
            {error}
          </div>
        )}

        <div className="mb-4 flex space-x-4">
          <select
            value={assetId || ''}
            onChange={(e) => setAssetId(e.target.value ? Number(e.target.value) : undefined)}
            className="border rounded px-3 py-2"
          >
            <option value="">All Assets</option>
            {assets.map((asset) => (
              <option key={asset.id} value={asset.id}>
                {asset.name}
              </option>
            ))}
          </select>

          <button onClick={() => fetchData()} className="bg-blue-500 text-white px-4 py-2 rounded">
            Apply Filters
          </button>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Asset
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Purchase Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Price
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Vendor
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Invoice
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Expiry Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Qty
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Notify
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {purchaseHistories.map((history) => (
                <tr key={history.id}>
                  <td className="px-6 py-4 whitespace-nowrap">{history.assetName}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {new Date(history.purchaseDate).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">₹{history.amount}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{history.vendor}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{history.invoiceNumber}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    {history.expiryDate ? new Date(history.expiryDate).toLocaleDateString() : 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">{history.qty}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{history.notify || 'No'}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      onClick={() => navigate(`/purchase-history/asset/${history.id}`)}
                      className="text-blue-600 hover:text-blue-900"
                    >
                      View
                    </button>
                    <button
                      onClick={() => navigate(`/purchase-history/edit/${history.id}`)}
                      className="text-yellow-600 hover:text-yellow-900 ml-4"
                    >
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
              {purchaseHistories.length === 0 && (
                <tr>
                  <td colSpan={7} className="px-6 py-4 text-center text-sm text-gray-500">
                    No purchase histories found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <Pagination
          currentPage={page}
          totalPages={Math.ceil(total / limit)}
          onPageChange={(newPage) => setPage(newPage)}
        />
      </div>
    </div>
  );
};

export default ListPurchaseHistory; 
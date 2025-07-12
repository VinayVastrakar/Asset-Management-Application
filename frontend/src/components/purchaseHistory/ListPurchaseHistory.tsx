import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { purchaseHistoryApi, PurchaseHistory, PurchaseHistoryResponse } from '../../api/purchaseHistory.api';
import { assetApi } from '../../api/asset.api';
import Pagination from '../common/Pagination';
import { saveAs } from 'file-saver';

const ListPurchaseHistory: React.FC = () => {
  const navigate = useNavigate();
  const [purchaseHistories, setPurchaseHistories] = useState<PurchaseHistory[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [limit] = useState(10);
  const [total, setTotal] = useState(1);
  const [totalCurrentValue, setTotalCurrentValue] = useState(0);
  const [assetId, setAssetId] = useState<number | undefined>();
  const [assets, setAssets] = useState<{ id: number; name: string }[]>([]);
  const [downloading, setDownloading] = useState(false);

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
      const response: PurchaseHistoryResponse = await purchaseHistoryApi.getPurchaseHistories({ page:page-1, limit, assetId});
      setPurchaseHistories(response.content);
      setTotal(response.totalElements);
      setTotalCurrentValue(response.totalCurrentValue);
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

  const handleExportExcel = async () => {
    setDownloading(true);
    try {
      const blob = await purchaseHistoryApi.downloadExcel({ assetId });
      saveAs(blob, 'purchase_history.xlsx');
    } catch (err) {
      alert('Failed to download Excel report');
    } finally {
      setDownloading(false);
    }
  };

  if (loading && purchaseHistories.length === 0) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  return (
    <div className="container mx-auto px-2 sm:px-4 py-8 overflow-x-hidden w-full">
      <div className="max-w-7xl mx-auto w-full">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4 w-full">
          <h1 className="text-2xl sm:text-3xl font-semibold text-gray-800">Purchase History</h1>
          <div className="flex flex-col sm:flex-row gap-2 w-full sm:w-auto">
            <button
              onClick={handleExportExcel}
              className="bg-green-600 text-white px-4 py-2 text-sm font-medium rounded hover:bg-green-700 w-full sm:w-auto disabled:opacity-60"
              disabled={downloading}
            >
              {downloading ? 'Exporting...' : 'Export to Excel'}
            </button>
            <button
              onClick={() => navigate('/purchase-history/add')}
              className="bg-primary text-white px-4 py-2 text-sm font-medium rounded hover:bg-primary-dark w-full sm:w-auto"
            >
              Add New Purchase
            </button>
          </div>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 text-sm w-full">
            {error}
          </div>
        )}

        {/* Total Current Value Summary */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6 w-full">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 w-full">
            <div>
              <h3 className="text-lg font-medium text-blue-900">Total Current Value</h3>
              <p className="text-sm text-blue-700">Sum of all current values on this page</p>
            </div>
            <div className="text-left sm:text-right">
              <p className="text-xl sm:text-2xl font-bold text-blue-900">₹{totalCurrentValue.toLocaleString()}</p>
            </div>
          </div>
        </div>

        <div className="mb-4 flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-4 w-full">
          <select
            value={assetId || ''}
            onChange={(e) => setAssetId(e.target.value ? Number(e.target.value) : undefined)}
            className="border rounded px-3 py-2 w-full sm:w-auto"
          >
            <option value="">All Assets</option>
            {assets.map((asset) => (
              <option key={asset.id} value={asset.id}>
                {asset.name}
              </option>
            ))}
          </select>

          <button onClick={() => fetchData()} className="bg-blue-500 text-white px-4 py-2 rounded w-full sm:w-auto">
            Apply Filters
          </button>
        </div>

        {/* Desktop Table View */}
        <div className="hidden lg:block bg-white rounded-lg shadow overflow-x-auto w-full">
          <table className="w-full divide-y divide-gray-200">
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
                  Current Value
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
            <tbody className="bg-white divide-y divide-gray-200 w-full">
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
                  <td className="px-6 py-4 whitespace-nowrap">₹{history.currentValue}</td>
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
                  <td colSpan={10} className="px-6 py-4 text-center text-sm text-gray-500">
                    No purchase histories found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Mobile Card View */}
        <div className="lg:hidden space-y-4 w-full">
          {purchaseHistories.map((history) => (
            <div key={history.id} className="bg-white rounded-lg shadow p-4 w-full">
              <div className="flex justify-between items-start mb-3 w-full">
                <h3 className="text-lg font-semibold text-gray-900">{history.assetName}</h3>
                <div className="flex space-x-2">
                  <button
                    onClick={() => navigate(`/purchase-history/asset/${history.id}`)}
                    className="text-blue-600 hover:text-blue-900 text-sm"
                  >
                    View
                  </button>
                  <button
                    onClick={() => navigate(`/purchase-history/edit/${history.id}`)}
                    className="text-yellow-600 hover:text-yellow-900 text-sm"
                  >
                    Edit
                  </button>
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-3 text-sm w-full">
                <div>
                  <span className="text-gray-500">Purchase Date:</span>
                  <p className="font-medium">{new Date(history.purchaseDate).toLocaleDateString()}</p>
                </div>
                <div>
                  <span className="text-gray-500">Price:</span>
                  <p className="font-medium">₹{history.amount}</p>
                </div>
                <div>
                  <span className="text-gray-500">Vendor:</span>
                  <p className="font-medium">{history.vendor}</p>
                </div>
                <div>
                  <span className="text-gray-500">Invoice:</span>
                  <p className="font-medium">{history.invoiceNumber}</p>
                </div>
                <div>
                  <span className="text-gray-500">Expiry Date:</span>
                  <p className="font-medium">
                    {history.expiryDate ? new Date(history.expiryDate).toLocaleDateString() : 'N/A'}
                  </p>
                </div>
                <div>
                  <span className="text-gray-500">Current Value:</span>
                  <p className="font-medium text-green-600">₹{history.currentValue}</p>
                </div>
                <div>
                  <span className="text-gray-500">Quantity:</span>
                  <p className="font-medium">{history.qty}</p>
                </div>
                <div>
                  <span className="text-gray-500">Notify:</span>
                  <p className="font-medium">{history.notify || 'No'}</p>
                </div>
              </div>
            </div>
          ))}
          
          {purchaseHistories.length === 0 && (
            <div className="bg-white rounded-lg shadow p-8 text-center w-full">
              <p className="text-gray-500">No purchase histories found</p>
            </div>
          )}
        </div>
 
        <div className="mt-6 w-full">
          <Pagination
            currentPage={page}
            totalPages={Math.ceil(total / limit)}
            onPageChange={(newPage) => setPage(newPage)}
          />
        </div>
      </div>
    </div>
  );
};

export default ListPurchaseHistory; 
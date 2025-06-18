import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { purchaseHistoryApi } from '../../api/purchaseHistory.api';
import { assetApi } from '../../api/asset.api';

interface PurchaseHistory {
  id: number;
  assetId: number;
  assetName: string;
  purchaseDate: string;
  amount: number;
  vendor: string;
  notify: string;
  expiryDate: string;
  invoiceNumber: string;
  warrantyPeriod: number;
  description?: string;
}

interface Asset {
  id: number;
  name: string;
  description: string;
  categoryName: string;
  status: string;
  imageUrl?: string;
}

const ViewPurchaseHistory: React.FC = () => {
  const { assetId } = useParams<{ assetId: string }>();
  const navigate = useNavigate();
  const [purchaseHistories, setPurchaseHistories] = useState<PurchaseHistory[]>([]);
  const [asset, setAsset] = useState<Asset | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!assetId) return;
      
      setLoading(true);
      setError(null);
      try {
        // Fetch asset details
        const assetResponse = await assetApi.getAssetById(parseInt(assetId));
        setAsset(assetResponse);

        // Fetch purchase histories
        const historyResponse = await purchaseHistoryApi.getPurchaseHistories({ assetId: parseInt(assetId) });
        setPurchaseHistories(historyResponse.data);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch data');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [assetId]);

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

  if (loading) return <div className="flex justify-center items-center h-64">Loading...</div>;
  if (error) return <div className="text-red-600 p-4">Error: {error}</div>;
  if (!asset) return <div className="text-gray-600 p-4">Asset not found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-semibold text-gray-800">{asset.name}</h1>
            <p className="text-gray-600 mt-1">Purchase History</p>
          </div>
          <div className="space-x-2">
            <button
              onClick={() => navigate(`/assets/${assetId}`)}
              className="px-4 py-2 bg-gray-100 text-gray-600 rounded hover:bg-gray-200 transition"
            >
              Back to Asset
            </button>
            <button
              onClick={() => navigate(`/purchase-history/add?assetId=${assetId}`)}
              className="px-4 py-2 bg-primary text-white rounded hover:bg-primary-dark transition"
            >
              Add Purchase Record
            </button>
          </div>
        </div>

        {/* Asset Summary */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h2 className="text-xl font-semibold mb-4">Asset Details</h2>
              <p><strong>Description:</strong> {asset.description}</p>
              <p><strong>Category:</strong> {asset.categoryName}</p>
              <p><strong>Status:</strong> {renderStatusBadge(asset.status)}</p>
            </div>
            {asset.imageUrl && (
              <div className="flex justify-center">
                <img
                  src={asset.imageUrl}
                  alt={asset.name}
                  className="h-48 object-contain rounded"
                />
              </div>
            )}
          </div>
        </div>

        {/* Purchase History Table */}
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Purchase Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Expiry Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Amount
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Vendor
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Invoice Number
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Warranty Period
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
              {purchaseHistories.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-6 py-4 text-center text-gray-500">
                    No purchase history found
                  </td>
                </tr>
              ) : (
                purchaseHistories.map((history) => (
                  <tr key={history.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {new Date(history.purchaseDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {new Date(history.expiryDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      ${history.amount.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {history.vendor}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {history.invoiceNumber}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {history.warrantyPeriod} months
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs font-semibold rounded-full ${
                        history.notify === 'Yes' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                      }`}>
                        {history.notify}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <button
                        onClick={() => navigate(`/purchase-history/edit/${history.id}`)}
                        className="text-yellow-600 hover:text-yellow-900 mr-4"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => navigate(`/purchase-history/${history.id}`)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default ViewPurchaseHistory;

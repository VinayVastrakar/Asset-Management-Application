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
  billUrl?: string;
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
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [history, setHistory] = useState<PurchaseHistory | null>(null);
  const [asset, setAsset] = useState<Asset | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      setLoading(true);
      setError(null);
      try {
        const response = await purchaseHistoryApi.getPurchaseHistoryById(Number(id));
        setHistory(response);

        // Fetch asset details
        const assetResponse = await assetApi.getAssetById(response.assetId);
        console.log(assetResponse);
        setAsset(assetResponse);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch purchase history');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

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
  if (!history) return <div className="text-gray-600 p-4">Purchase history not found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-xl mx-auto bg-white rounded-lg shadow p-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-semibold text-gray-800">Purchase History Details</h1>
          <div className="space-x-2">
            <button
              onClick={() => navigate(-1)}
              className="px-4 py-2 bg-gray-100 text-gray-600 rounded hover:bg-gray-200 transition"
            >
              Back
            </button>
            <button
              onClick={() => navigate(`/purchase-history/edit/${id}`)}
              className="px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600 transition"
            >
              Edit
            </button>
          </div>
        </div>
        <div className="space-y-4">
          <div>
            <span className="font-medium">Asset:</span> {history.assetName}
          </div>
          <div>
            <span className="font-medium">Purchase Date:</span> {new Date(history.purchaseDate).toLocaleDateString()}
          </div>
          <div>
            <span className="font-medium">Expiry Date:</span> {new Date(history.expiryDate).toLocaleDateString()}
          </div>
          <div>
            <span className="font-medium">Amount:</span> ₹{history.amount.toFixed(2)}
          </div>
          <div>
            <span className="font-medium">Vendor:</span> {history.vendor}
          </div>
          <div>
            <span className="font-medium">Invoice Number:</span> {history.invoiceNumber}
          </div>
          <div>
            <span className="font-medium">Warranty Period:</span> {history.warrantyPeriod} months
          </div>
          <div>
            <span className="font-medium">Notify:</span> {history.notify}
          </div>
          <div>
            <span className="font-medium">Description:</span> {history.description || '-'}
          </div>
          {history.billUrl && (
            <div>
              <span className="font-medium">Bill PDF:</span>{' '}
              <a
                href={history.billUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-blue-600 hover:underline ml-2"
                download
              >
                Download PDF
              </a>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ViewPurchaseHistory;

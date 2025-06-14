import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { purchaseHistoryApi } from '../../api/purchaseHistory.api';
import { assetApi } from '../../api/asset.api';

interface Asset {
  id: number;
  name: string;
}

const AddPurchaseHistory: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [assets, setAssets] = useState<Asset[]>([]);

  const [formData, setFormData] = useState({
    assetId: '',
    purchaseDate: '',
    amount: '',
    vendor: '',
    invoiceNumber: '',
    warrantyPeriod: '',
    description: '',
    expiryDate: '',
    notify: 'No'
  });

  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchAssets = async () => {
      try {
        const response = await assetApi.getAssets({ page: 0, limit: 1000 });
        setAssets(response.data.data);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch assets');
      }
    };

    fetchAssets();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};
    if (!formData.assetId) errors.assetId = 'Asset is required';
    if (!formData.purchaseDate) errors.purchaseDate = 'Purchase date is required';
    if (!formData.amount) errors.amount = 'Purchase price is required';
    if (!formData.vendor) errors.vendor = 'Vendor name is required';
    if (!formData.invoiceNumber) errors.invoiceNumber = 'Invoice number is required';
    if (!formData.warrantyPeriod) errors.warrantyPeriod = 'Warranty period is required';

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      await purchaseHistoryApi.createPurchaseHistory({
        assetId: Number(formData.assetId),
        assetName: assets.find(a => a.id === Number(formData.assetId))?.name || '',
        purchaseDate: formData.purchaseDate,
        amount: Number(formData.amount),
        vendor: formData.vendor,
        invoiceNumber: formData.invoiceNumber,
        warrantyPeriod: Number(formData.warrantyPeriod),
        description: formData.description,
        expiryDate: formData.expiryDate,
        notify: formData.notify
      });
      navigate('/purchase-history');
    } catch (err: any) {
      setError(err.message || 'Failed to create purchase history');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-xl mx-auto">
        <h1 className="text-3xl font-semibold mb-6 text-gray-800">Add Purchase History</h1>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-6 space-y-4">
          <div>
            <label htmlFor="assetId" className="block text-sm font-medium text-gray-700 mb-1">
              Asset
            </label>
            <select
              id="assetId"
              name="assetId"
              value={formData.assetId}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${
                formErrors.assetId ? 'border-red-500' : 'border-gray-300'
              }`}
            >
              <option value="">Select an asset</option>
              {assets.map((asset) => (
                <option key={asset.id} value={asset.id}>
                  {asset.name}
                </option>
              ))}
            </select>
            {formErrors.assetId && <p className="text-sm text-red-600">{formErrors.assetId}</p>}
          </div>

          <div>
            <label htmlFor="purchaseDate" className="block text-sm font-medium text-gray-700 mb-1">
              Purchase Date
            </label>
            <input
              type="date"
              id="purchaseDate"
              name="purchaseDate"
              value={formData.purchaseDate}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${
                formErrors.purchaseDate ? 'border-red-500' : 'border-gray-300'
              }`}
            />
            {formErrors.purchaseDate && <p className="text-sm text-red-600">{formErrors.purchaseDate}</p>}
          </div>

          <div>
            <label htmlFor="amount" className="block text-sm font-medium text-gray-700 mb-1">
              Purchase Price
            </label>
            <input
              type="number"
              id="amount"
              name="amount"
              value={formData.amount}
              onChange={handleChange}
              placeholder="0.00"
              step="0.01"
              className={`w-full px-3 py-2 border rounded text-sm ${
                formErrors.amount ? 'border-red-500' : 'border-gray-300'
              }`}
            />
            {formErrors.amount && <p className="text-sm text-red-600">{formErrors.amount}</p>}
          </div>

          <div>
            <label htmlFor="vendor" className="block text-sm font-medium text-gray-700 mb-1">
              Vendor Name
            </label>
            <input
              type="text"
              id="vendor"
              name="vendor"
              value={formData.vendor}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${
                formErrors.vendor ? 'border-red-500' : 'border-gray-300'
              }`}
            />
            {formErrors.vendor && <p className="text-sm text-red-600">{formErrors.vendor}</p>}
          </div>

          <div>
            <label htmlFor="invoiceNumber" className="block text-sm font-medium text-gray-700 mb-1">
              Invoice Number
            </label>
            <input
              type="text"
              id="invoiceNumber"
              name="invoiceNumber"
              value={formData.invoiceNumber}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${
                formErrors.invoiceNumber ? 'border-red-500' : 'border-gray-300'
              }`}
            />
            {formErrors.invoiceNumber && <p className="text-sm text-red-600">{formErrors.invoiceNumber}</p>}
          </div>

          <div>
            <label htmlFor="warrantyPeriod" className="block text-sm font-medium text-gray-700 mb-1">
              Warranty Period (months)
            </label>
            <input
              type="number"
              id="warrantyPeriod"
              name="warrantyPeriod"
              value={formData.warrantyPeriod}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${
                formErrors.warrantyPeriod ? 'border-red-500' : 'border-gray-300'
              }`}
            />
            {formErrors.warrantyPeriod && <p className="text-sm text-red-600">{formErrors.warrantyPeriod}</p>}
          </div>

          <div>
            <label htmlFor="expiryDate" className="block text-sm font-medium text-gray-700 mb-1">
              Expiry Date
            </label>
            <input
              type="date"
              id="expiryDate"
              name="expiryDate"
              value={formData.expiryDate}
              onChange={handleChange}
              min={formData.purchaseDate}
              className={`w-full px-3 py-2 border rounded text-sm ${
                formErrors.expiryDate ? 'border-red-500' : 'border-gray-300'
              }`}
            />
            {formErrors.expiryDate && <p className="text-sm text-red-600">{formErrors.expiryDate}</p>}
          </div>

          <div>
            <label htmlFor="notify" className="block text-sm font-medium text-gray-700 mb-1">
              Notify
            </label>
            <select
              id="notify"
              name="notify"
              value={formData.notify}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
            >
              <option value="Yes">Yes</option>
              <option value="No">No</option>
            </select>
          </div>

          <div>
            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
            />
          </div>

          <div className="flex justify-between pt-4">
            <button
              type="button"
              onClick={() => navigate('/purchase-history')}
              className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-100"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="bg-primary text-white px-4 py-2 text-sm font-medium rounded hover:bg-primary-dark disabled:opacity-50"
            >
              {loading ? 'Adding...' : 'Add Purchase History'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddPurchaseHistory; 
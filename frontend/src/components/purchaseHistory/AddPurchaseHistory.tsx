import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { purchaseHistoryApi } from '../../api/purchaseHistory.api';
import { assetApi } from '../../api/asset.api';

interface Asset {
  id: number;
  name: string;
}

const defaultFormData = {
  assetId: '',
  purchaseDate: '',
  amount: '',
  vendor: '',
  invoiceNumber: '',
  warrantyPeriod: '',
  qty: 1,
  description: '',
  expiryDate: '',
  notify: 'No',
};

const AddPurchaseHistory: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [billFile, setBillFile] = useState<File | null>(null);
  const [formData, setFormData] = useState(defaultFormData);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    (async () => {
      try {
        const response = await assetApi.getAssets({ page: 0, limit: 1000 });
        setAssets(response.data.data);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch assets');
      }
    })();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? Number(value) : value,
    }));
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};
    if (!formData.assetId) errors.assetId = 'Asset is required';
    if (!formData.purchaseDate) errors.purchaseDate = 'Purchase date is required';
    if (!formData.amount) errors.amount = 'Purchase price is required';
    if (!formData.vendor) errors.vendor = 'Vendor name is required';
    if (!formData.invoiceNumber) errors.invoiceNumber = 'Invoice number is required';
    if (!formData.warrantyPeriod) errors.warrantyPeriod = 'Warranty period is required';
    if (formData.qty < 1) errors.qty = 'Quantity must be at least 1';

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      const payload = new FormData();
      payload.append('purchaseHistory', JSON.stringify({
        ...formData,
        assetId: Number(formData.assetId),
        amount: Number(formData.amount),
        warrantyPeriod: Number(formData.warrantyPeriod),
      }));
      if (billFile) payload.append('file', billFile);

      await purchaseHistoryApi.createPurchaseHistoryWithBill(payload);
      navigate('/purchase-history');
    } catch (err: any) {
      setError(err.message || 'Failed to create purchase history');
    } finally {
      setLoading(false);
    }
  };

  const renderInput = (
    name: string,
    label: string,
    type: 'text' | 'number' | 'date' = 'text',
    extraProps: Record<string, any> = {}
  ) => (
    <div>
      <label htmlFor={name} className="block text-sm font-medium text-gray-700 mb-1">
        {label}
      </label>
      <input
        id={name}
        name={name}
        type={type}
        value={formData[name as keyof typeof formData] as any}
        onChange={handleChange}
        {...extraProps}
        className={`w-full px-3 py-2 border rounded text-sm ${
          formErrors[name] ? 'border-red-500' : 'border-gray-300'
        }`}
      />
      {formErrors[name] && <p className="text-sm text-red-600">{formErrors[name]}</p>}
    </div>
  );

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
          {/* Asset dropdown */}
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
              {assets.map(asset => (
                <option key={asset.id} value={asset.id}>
                  {asset.name}
                </option>
              ))}
            </select>
            {formErrors.assetId && <p className="text-sm text-red-600">{formErrors.assetId}</p>}
          </div>

          {renderInput('purchaseDate', 'Purchase Date', 'date')}
          {renderInput('amount', 'Purchase Price', 'number', { step: '0.01', placeholder: '0.00' })}
          {renderInput('vendor', 'Vendor Name')}
          {renderInput('qty', 'Quantity', 'number', { min: 1 })}
          {renderInput('invoiceNumber', 'Invoice Number')}
          {renderInput('warrantyPeriod', 'Warranty Period (months)', 'number')}
          {renderInput('expiryDate', 'Expiry Date', 'date', { min: formData.purchaseDate })}
          
          {/* Notify dropdown */}
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

          {/* Description */}
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

          {/* Bill Upload */}
          <div>
            <label htmlFor="billFile" className="block text-sm font-medium text-gray-700 mb-1">
              Bill (PDF)
            </label>
            <input
              type="file"
              id="billFile"
              name="billFile"
              accept="application/pdf"
              onChange={e => setBillFile(e.target.files?.[0] || null)}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
            />
          </div>

          {/* Actions */}
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

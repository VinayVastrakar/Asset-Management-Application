import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { depreciationRateApi, DepreciationRateRequest } from '../../api/depreciationRate.api';

const initialForm: DepreciationRateRequest = {
  categoryId: 0,
  assetType: '',
  financialYear: '',
  depreciationPercentage: 0,
  depreciationMethod: '',
  usefulLifeYears: undefined,
  residualValuePercentage: undefined,
  effectiveFromDate: '',
  effectiveToDate: '',
};

const AddDepreciationRate: React.FC = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState<DepreciationRateRequest>(initialForm);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const validate = () => {
    const errs: Record<string, string> = {};
    if (!form.categoryId) errs.categoryId = 'Category ID is required';
    if (!form.financialYear) errs.financialYear = 'Financial year is required';
    if (!form.depreciationPercentage || form.depreciationPercentage < 0 || form.depreciationPercentage > 100) errs.depreciationPercentage = 'Depreciation % must be 0-100';
    if (!form.depreciationMethod) errs.depreciationMethod = 'Method is required';
    if (!form.effectiveFromDate) errs.effectiveFromDate = 'Effective from date is required';
    return errs;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: name === 'categoryId' || name === 'depreciationPercentage' || name === 'usefulLifeYears' || name === 'residualValuePercentage' ? Number(value) : value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;
    setLoading(true);
    setError(null);
    try {
      await depreciationRateApi.createDepreciationRate(form);
      navigate('/depreciation-rates');
    } catch (err: any) {
      setError(err.message || 'Failed to add depreciation rate');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-xl mx-auto bg-white rounded-lg shadow p-6">
        <h1 className="text-2xl font-semibold mb-6 text-gray-800">Add Depreciation Rate</h1>
        {error && <div className="text-red-600 mb-4">{error}</div>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Category ID</label>
            <input
              type="number"
              name="categoryId"
              value={form.categoryId || ''}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${errors.categoryId ? 'border-red-500' : 'border-gray-300'}`}
            />
            {errors.categoryId && <p className="text-sm text-red-600">{errors.categoryId}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Asset Type (optional)</label>
            <input
              type="text"
              name="assetType"
              value={form.assetType || ''}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Financial Year (e.g. 2023-24)</label>
            <input
              type="text"
              name="financialYear"
              value={form.financialYear}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${errors.financialYear ? 'border-red-500' : 'border-gray-300'}`}
            />
            {errors.financialYear && <p className="text-sm text-red-600">{errors.financialYear}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Depreciation %</label>
            <input
              type="number"
              name="depreciationPercentage"
              value={form.depreciationPercentage}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${errors.depreciationPercentage ? 'border-red-500' : 'border-gray-300'}`}
              min={0}
              max={100}
              step={0.01}
            />
            {errors.depreciationPercentage && <p className="text-sm text-red-600">{errors.depreciationPercentage}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Method</label>
            <select
              name="depreciationMethod"
              value={form.depreciationMethod}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${errors.depreciationMethod ? 'border-red-500' : 'border-gray-300'}`}
            >
              <option value="">Select method</option>
              <option value="SLM">SLM (Straight Line)</option>
              <option value="WDV">WDV (Written Down Value)</option>
            </select>
            {errors.depreciationMethod && <p className="text-sm text-red-600">{errors.depreciationMethod}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Useful Life (years, optional)</label>
            <input
              type="number"
              name="usefulLifeYears"
              value={form.usefulLifeYears || ''}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
              min={1}
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Residual Value % (optional)</label>
            <input
              type="number"
              name="residualValuePercentage"
              value={form.residualValuePercentage || ''}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
              min={0}
              max={100}
              step={0.01}
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Effective From</label>
            <input
              type="date"
              name="effectiveFromDate"
              value={form.effectiveFromDate}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${errors.effectiveFromDate ? 'border-red-500' : 'border-gray-300'}`}
            />
            {errors.effectiveFromDate && <p className="text-sm text-red-600">{errors.effectiveFromDate}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Effective To (optional)</label>
            <input
              type="date"
              name="effectiveToDate"
              value={form.effectiveToDate || ''}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
            />
          </div>
          <div className="flex justify-between pt-4">
            <button
              type="button"
              onClick={() => navigate('/depreciation-rates')}
              className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-100"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="bg-primary text-white px-4 py-2 text-sm font-medium rounded hover:bg-primary-dark disabled:opacity-50"
            >
              {loading ? 'Adding...' : 'Add Rate'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddDepreciationRate; 
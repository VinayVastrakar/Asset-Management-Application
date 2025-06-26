import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { depreciationRateApi, DepreciationRateRequest } from '../../api/depreciationRate.api';
import { categoryApi, Category } from '../../api/category.api';

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

// Helper to get past and current financial years
const getPastAndCurrentFinancialYears = (startYear: number) => {
  const years: string[] = [];
  const today = new Date();
  let currentFYStart = today.getFullYear();
  // If before April, current FY is previous year
  if (today.getMonth() < 3) {
    currentFYStart = currentFYStart - 1;
  }
  for (let fy = startYear; fy <= currentFYStart; fy++) {
    const fyEnd = (fy + 1).toString().slice(-2);
    years.push(`${fy}-${fyEnd}`);
  }
  return years;
};
const financialYears = getPastAndCurrentFinancialYears(2020);

const EditDepreciationRate: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form, setForm] = useState<DepreciationRateRequest>(initialForm);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fetching, setFetching] = useState(true);
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      setFetching(true);
      try {
        const data = await depreciationRateApi.getDepreciationRateById(Number(id));
        setForm({
          categoryId: data.data.categoryId,
          assetType: data.data.assetType || '',
          financialYear: data.data.financialYear,
          depreciationPercentage: data.data.depreciationPercentage,
          depreciationMethod: data.data.depreciationMethod,
          usefulLifeYears: data.data.usefulLifeYears,
          residualValuePercentage: data.data.residualValuePercentage,
          effectiveFromDate: data.data.effectiveFromDate,
          effectiveToDate: data.data.effectiveToDate || '',
        });
      } catch (err: any) {
        setError(err.message || 'Failed to fetch depreciation rate');
      } finally {
        setFetching(false);
      }
    };
    fetchData();
  }, [id]);

  useEffect(() => {
    // Fetch categories for dropdown
    const fetchCategories = async () => {
      try {
        const data = await categoryApi.getCategories();
        setCategories(data || []);
      } catch {
        setCategories([]);
      }
    };
    fetchCategories();
  }, []);

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
    if (name === 'depreciationPercentage') {
      // Remove leading zeros and store as number
      const sanitized = value.replace(/^0+(?=\d)/, '');
      setForm(f => ({ ...f, [name]: Number(sanitized) }));
    } else if (name === 'categoryId' || name === 'usefulLifeYears' || name === 'residualValuePercentage') {
      setForm(f => ({ ...f, [name]: Number(value) }));
    } else {
      setForm(f => ({ ...f, [name]: value }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length > 0 || !id) return;
    setLoading(true);
    setError(null);
    try {
      await depreciationRateApi.updateDepreciationRate(Number(id), form);
      navigate('/depreciation-rates');
    } catch (err: any) {
      setError(err.message || 'Failed to update depreciation rate');
    } finally {
      setLoading(false);
    }
  };

  if (fetching) {
    return <div className="flex justify-center items-center h-64">Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-xl mx-auto bg-white rounded-lg shadow p-6">
        <h1 className="text-2xl font-semibold mb-6 text-gray-800">Edit Depreciation Rate</h1>
        {error && <div className="text-red-600 mb-4">{error}</div>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Category</label>
            <select
              name="categoryId"
              value={form.categoryId}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${errors.categoryId ? 'border-red-500' : 'border-gray-300'}`}
            >
              <option value="">Select category</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
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
            <label className="block text-sm font-medium mb-1">Financial Year</label>
            <select
              name="financialYear"
              value={form.financialYear}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded text-sm ${errors.financialYear ? 'border-red-500' : 'border-gray-300'}`}
            >
              <option value="">Select financial year</option>
              {financialYears.map(fy => (
                <option key={fy} value={fy}>{fy}</option>
              ))}
            </select>
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
              <option value="PRO_RATA">PRO_RATA (Pro-rata/Time Apportioned)</option>
              {/* <option value="SLM">SLM (Straight Line)</option>
              <option value="WDV">WDV (Written Down Value)</option> */}
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
              {loading ? 'Updating...' : 'Update Rate'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditDepreciationRate; 
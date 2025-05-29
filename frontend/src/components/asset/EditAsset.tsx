import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import { RootState, AppDispatch } from '../../redux/store';
import { clearCurrentAsset, fetchAssetById, updateAsset } from '../../redux/slices/assetSlice';
import { fetchCategories } from '../../redux/slices/categorySlice';

const EditAsset: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { currentAsset: asset, loading: assetLoading, updating, error: assetError } = useSelector((state: RootState) => state.assets);
  const { categories, loading: categoryLoading, error: categoryError } = useSelector((state: RootState) => state.categories);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    categoryId: '',
    purchaseDate: '',
    expiryDate: '',
    warrantyPeriod: ''
  });

  const [image, setImage] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (id) {
      dispatch(fetchAssetById(parseInt(id)));
    }
    dispatch(fetchCategories());
  }, [dispatch, id]);

  useEffect(() => {
    if (asset) {
      setFormData({
        name: asset.name,
        description: asset.description,
        categoryId: asset.categoryId !== undefined && asset.categoryId !== null ? asset.categoryId.toString() : '', 
        purchaseDate: asset.purchaseDate,
        expiryDate: asset.expiryDate,
        warrantyPeriod: asset.warrantyPeriod.toString()
      });
      if (asset.imageUrl) {
        setImagePreview(asset.imageUrl);
      }
    }
  }, [asset]);

  // console.log("formdata---->",formData);
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImage(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};
    if (!formData.name.trim()) errors.name = 'Name is required';
    if (!formData.description.trim()) errors.description = 'Description is required';
    if (!formData.categoryId) errors.categoryId = 'Category is required';
    if (!formData.purchaseDate) errors.purchaseDate = 'Purchase date is required';
    if (!formData.expiryDate) errors.expiryDate = 'Expiry date is required';
    if (!formData.warrantyPeriod) errors.warrantyPeriod = 'Warranty period is required';

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm() || !id) return;

    const formPayload = new FormData();
    formPayload.append('asset', JSON.stringify(formData));
    if (image) formPayload.append('file', image);

    try {
      await dispatch(updateAsset({ id: parseInt(id), formData: formPayload }));
      navigate('/assets');
    } catch (err) {
      console.error('Failed to update asset:', err);
    }
  };

  if (assetLoading && !asset) {
    return <div className="flex justify-center items-center h-screen">Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-xl mx-auto">
        <h1 className="text-3xl font-semibold mb-6 text-gray-800">Edit Asset</h1>

        {(assetError || categoryError) && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4 text-sm">
            {assetError || categoryError}
          </div>
        )}

        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-6 space-y-4">
          {[
            { label: 'Name', name: 'name', type: 'text', placeholder: 'Office Laptop' },
            { label: 'Description', name: 'description', type: 'textarea', placeholder: 'Brief description of the asset' },
            { label: 'Purchase Date', name: 'purchaseDate', type: 'date' },
            { label: 'Expiry Date', name: 'expiryDate', type: 'date' },
            { label: 'Warranty Period (in months)', name: 'warrantyPeriod', type: 'number', placeholder: '12' }
          ].map((field) => (
            <div key={field.name}>
              <label htmlFor={field.name} className="block text-sm font-medium text-gray-700 mb-1">
                {field.label}
              </label>
              {field.type === 'textarea' ? (
                <textarea
                  id={field.name}
                  name={field.name}
                  value={(formData as any)[field.name]}
                  onChange={handleChange}
                  placeholder={field.placeholder}
                  className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
                  rows={3}
                  required
                />
              ) : (
                <input
                  id={field.name}
                  name={field.name}
                  type={field.type}
                  value={(formData as any)[field.name]}
                  onChange={handleChange}
                  placeholder={field.placeholder}
                  required
                  className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
                />
              )}
              {formErrors[field.name] && <p className="text-sm text-red-600">{formErrors[field.name]}</p>}
            </div>
          ))}

          <div>
            <label htmlFor="categoryId" className="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select
              id="categoryId"
              name="categoryId"
              value={formData.categoryId}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
              disabled={categoryLoading}
            >
              <option value="">Select a category</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
            {formErrors.categoryId && <p className="text-sm text-red-600">{formErrors.categoryId}</p>}
          </div>

          <div>
            <label htmlFor="image" className="block text-sm font-medium text-gray-700 mb-1">Image</label>
            <input
              id="image"
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              className="w-full px-3 py-2 border border-gray-300 rounded text-sm"
            />
            {imagePreview && (
              <img src={imagePreview} alt="Preview" className="mt-2 w-32 h-32 object-cover rounded" />
            )}
          </div>

          <div className="flex items-center justify-between pt-4">
            <button
              type="button"
              onClick={() => navigate('/assets')}
              className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-100"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={updating}
              className="bg-primary text-white px-4 py-2 text-sm font-medium rounded hover:bg-primary-dark disabled:opacity-50"
            >
              {updating ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditAsset;

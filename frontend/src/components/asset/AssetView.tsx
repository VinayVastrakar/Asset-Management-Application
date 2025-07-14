import React, { useCallback, useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../../redux/store';
import { fetchAssetById } from '../../redux/slices/assetSlice';
import { assetApi } from 'api/asset.api';
import userApi from 'api/user.api';
import { saveAs } from 'file-saver';

const AssetView: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const { currentAsset: asset, loading, error } = useSelector((state: RootState) => state.assets);

  const [isModalOpen, setModalOpen] = useState(false);
  const [users, setUsers] = useState<{ id: number; name: string }[]>([]);
  const [selectedUser, setSelectedUser] = useState<number | null>(null);
  const [assigning, setAssigning] = useState(false);
  const [exportLoading, setExportLoading] = useState(false);
  const [stolenNotes, setStolenNotes] = useState('');
  const [disposedNotes, setDisposedNotes] = useState('');
  const [stolenLoading, setStolenLoading] = useState(false);
  const [disposedLoading, setDisposedLoading] = useState(false);
  const [showStolenModal, setShowStolenModal] = useState(false);
  const [showDisposedModal, setShowDisposedModal] = useState(false);

  useEffect(() => {
    if (id) {
      dispatch(fetchAssetById(parseInt(id)));
    }
  }, [dispatch, id]);

  const openModal = async () => {
    setModalOpen(true);
    try {
      const res = await userApi.getActiveUsers();
      setUsers(res.data.users);
    } catch (err) {
      console.error('Failed to load users', err);
    }
  };

  const handleExport = async () => {
    if (!asset?.id) return;
    
    setExportLoading(true);
    try {
      const blob = await assetApi.exportAssetAssignmentHistory(asset.id, undefined);
      const fileName = `asset_assignment_history_${asset.name.replace(/[^a-zA-Z0-9]/g, '_')}_${new Date().toISOString().split('T')[0]}.xlsx`;
      saveAs(blob, fileName);
    } catch (err: any) {
      console.error('Export failed:', err);
      alert('Failed to export asset assignment history. Please try again.');
    } finally {
      setExportLoading(false);
    }
  };

  const returnAsset = async (id: number) => {
    const confirmed = window.confirm('Are you sure you want to return this asset?');
    if (!confirmed) return;
  
    try {
      // Optionally show a loading state
      setAssigning(true);
  
      await assetApi.returnAsset(id);
  
      // Optionally show success message
      alert('Asset successfully returned.');
  
      // Refresh asset details after returning
      // dispatch(fetchAssetById(id));
      navigate('/assets');
    } catch (err) {
      console.error('Failed to return asset', err);
      alert('Failed to return the asset. Please try again.');
    } finally {
      setAssigning(false);
    }
  };

  const assignUser = async () => {
    if (!selectedUser || !asset?.id) return;
    setAssigning(true);
    try {
      await assetApi.assignAssetToUser(asset.id, selectedUser);
      setModalOpen(false);
      // dispatch(fetchAssetById(asset.id)); // refresh asset view
      navigate('/assets');
    } catch (err) {
      console.error('Failed to assign user', err);
    } finally {
      setAssigning(false);
    }
  };

  const handleMarkStolen = async () => {
    if (!asset?.id) return;
    setStolenLoading(true);
    try {
      await assetApi.markAssetAsStolen(asset.id, stolenNotes);
      alert('Asset marked as stolen!');
      setShowStolenModal(false);
      navigate('/assets');
    } catch (err) {
      alert('Failed to mark as stolen');
    } finally {
      setStolenLoading(false);
    }
  };

  const handleMarkDisposed = async () => {
    if (!asset?.id) return;
    setDisposedLoading(true);
    try {
      await assetApi.markAssetAsDisposed(asset.id, disposedNotes);
      alert('Asset marked as disposed!');
      setShowDisposedModal(false);
      navigate('/assets');
    } catch (err) {
      alert('Failed to mark as disposed');
    } finally {
      setDisposedLoading(false);
    }
  };

  const renderStatusBadge = useCallback((status: string) => {
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
  }, []);

  if (loading) return <div className="flex justify-center items-center h-64">Loading...</div>;
  if (error) return <div className="text-red-600 p-4">{error}</div>;
  if (!asset) return <div className="text-gray-600 p-4">Asset not found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        {/* Back Button */}
        <button
          onClick={() => navigate('/assets')}
          className="mb-4 flex items-center space-x-2 px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition"
        >
          <svg className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
          <span>Back</span>
        </button>
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-semibold text-gray-800">{asset.name}</h1>
          <div className="space-x-2 flex items-center">
            {renderStatusBadge(asset.status)}

            {/* Export Assignment History Button */}
            <button
              onClick={handleExport}
              disabled={exportLoading}
              title="Export assignment history for this asset"
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition disabled:opacity-50 flex items-center space-x-2"
            >
              {exportLoading ? (
                <>
                  <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  <span>Exporting...</span>
                </>
              ) : (
                <>
                  <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span>Export History</span>
                </>
              )}
            </button>

            {asset.status === 'AVAILABLE' && (
              <button
                onClick={openModal}
                className="px-4 py-2 bg-blue-100 text-blue-600 rounded hover:bg-blue-200 transition"
              >
                Assign User
              </button>
            )}

            {asset.status === 'ASSIGNED' && (
              <button
                onClick={() => returnAsset(asset.id)}
                className="px-4 py-2 bg-green-100 text-green-600 rounded hover:bg-green-200 transition"
              >
                Return Asset
              </button>
            )}

            {asset.status !== 'STOLEN' && asset.status !== 'DISPOSED' && (
              <button
                onClick={() => setShowStolenModal(true)}
                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
              >
                Mark as Stolen
              </button>
            )}
            {asset.status !== 'DISPOSED' && asset.status !== 'STOLEN' && (
              <button
                onClick={() => setShowDisposedModal(true)}
                className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition"
              >
                Mark as Disposed
              </button>
            )}

            <button
              onClick={() => navigate(`/assets/edit/${asset.id}`)}
              className="px-4 py-2 bg-yellow-100 text-yellow-600 rounded hover:bg-yellow-200 transition"
            >
              Edit Asset
            </button>

            <button
              onClick={() => navigate(`/purchase-history/asset/${asset.id}`)}
              className="px-4 py-2 bg-purple-100 text-purple-600 rounded hover:bg-purple-200 transition"
            >
              View Purchase History
            </button>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          {asset.imageUrl && (
            <div className="h-96 overflow-hidden">
              <img
                src={asset.imageUrl}
                alt={asset.name}
                className="w-full h-full object-contain"
              />
            </div>
          )}

          <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h2 className="text-xl font-semibold mb-4">Asset Details</h2>
              <p><strong>Description:</strong> {asset.description}</p>
              <p><strong>Status:</strong> {asset.status}</p>
              <p><strong>Category:</strong> {asset.categoryName}</p>
              <p><strong>Warranty Period:</strong> {asset.warrantyPeriod} months</p>
            </div>
            <div>
              <h2 className="text-xl font-semibold mb-4">Assignment Details</h2>
              <p><strong>Assigned To:</strong> {asset.assignedToUserName || 'Not Assigned'}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-semibold mb-4">Assign User</h2>
            <select
              value={selectedUser || ''}
              onChange={(e) => setSelectedUser(Number(e.target.value))}
              className="w-full border border-gray-300 rounded p-2 mb-4"
            >
              <option value="">Select a user</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.name}
                </option>
              ))}
            </select>

            <div className="flex justify-end space-x-2">
              <button
                onClick={() => setModalOpen(false)}
                className="px-4 py-2 bg-gray-100 text-gray-600 rounded hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={assignUser}
                disabled={assigning || !selectedUser}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                {assigning ? 'Assigning...' : 'Assign'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Stolen Modal */}
      {showStolenModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-semibold mb-4">Mark as Stolen</h2>
            <textarea
              value={stolenNotes}
              onChange={e => setStolenNotes(e.target.value)}
              className="w-full border border-gray-300 rounded p-2 mb-4"
              placeholder="Enter notes (optional)"
            />
            <div className="flex justify-end space-x-2">
              <button
                onClick={() => setShowStolenModal(false)}
                className="px-4 py-2 bg-gray-100 text-gray-600 rounded hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={handleMarkStolen}
                disabled={stolenLoading}
                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
              >
                {stolenLoading ? 'Marking...' : 'Mark as Stolen'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Disposed Modal */}
      {showDisposedModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-semibold mb-4">Mark as Disposed</h2>
            <textarea
              value={disposedNotes}
              onChange={e => setDisposedNotes(e.target.value)}
              className="w-full border border-gray-300 rounded p-2 mb-4"
              placeholder="Enter notes (optional)"
            />
            <div className="flex justify-end space-x-2">
              <button
                onClick={() => setShowDisposedModal(false)}
                className="px-4 py-2 bg-gray-100 text-gray-600 rounded hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={handleMarkDisposed}
                disabled={disposedLoading}
                className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
              >
                {disposedLoading ? 'Marking...' : 'Mark as Disposed'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AssetView;

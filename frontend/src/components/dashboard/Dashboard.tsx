import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { dashboardApi, DashboardStats } from "../../api/dashboard.api";
import { Alert } from "../common/Alert";

const Dashboard: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);
        setError(null);
        setSuccess(null);
        const response = await dashboardApi.getStats();
        setStats(response.data.data);
        setSuccess('Dashboard data loaded successfully');
        setTimeout(() => setSuccess(null), 2000);
      } catch (err: any) {
        const message = err.response?.data?.message || "Failed to fetch dashboard statistics";
        setError(message);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
      </div>

      {error && (
        <div className="mb-6">
          <Alert type="error" message={error} />
        </div>
      )}
      {success && (
        <div className="mb-6">
          <Alert type="success" message={success} />
        </div>
      )}

      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-lg font-semibold text-gray-900 mb-2">
              Total Assets
            </h2>
            <p className="text-3xl font-bold text-primary">
              {stats.totalAssets}
            </p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-lg font-semibold text-gray-900 mb-2">
              Total Users
            </h2>
            <p className="text-3xl font-bold text-primary">
              {stats.totalUsers}
            </p>
          </div>
          {user?.role === "ADMIN" && (
            <>
              <div className="bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">
                  Expiring Soon Assets
                </h2>
                <p className="text-3xl font-bold text-primary">
                  {stats.expiringSoonCount}
                </p>
              </div>
              <div className="bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">
                  Expired Assets
                </h2>
                <p className="text-3xl font-bold text-primary">
                  {stats.expiredAssets}
                </p>
              </div>
              <div className="bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">
                  Assigned Assets
                </h2>
                <p className="text-3xl font-bold text-primary">
                  {stats.assignedAssets}
                </p>
              </div>
              <div className="bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">
                  Non Assigned Assets
                </h2>
                <p className="text-3xl font-bold text-primary">
                  {stats.nonAssignedAssets}
                </p>
              </div>
            </>
          )}
           <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">
              Assets by Category
            </h2>
            <ul className="space-y-3">
              {stats.categoryWise.map((cat) => (
                <li
                  key={cat.category}
                  className="flex justify-between items-center py-2 border-b last:border-b-0"
                >
                  <span className="text-gray-700">{cat.category}</span>
                  <span className="font-medium text-primary">{cat.count}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;

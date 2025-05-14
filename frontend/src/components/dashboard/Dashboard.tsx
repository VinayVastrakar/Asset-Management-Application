import React from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/store";

const Dashboard: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

  // Fetch stats from API (useEffect + useState or RTK Query)
  // Example stats:
  const stats = {
    totalAssets: 120,
    totalUsers: 15,
    categoryWise: [
      { category: "Laptops", count: 40 },
      { category: "Mobiles", count: 30 },
      // ...
    ],
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold">Dashboard</h1>
          <p className="text-gray-600">Welcome, {user?.name} ({user?.role})</p>
        </div>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-4 rounded shadow">
          <h2 className="text-lg font-semibold">Total Assets</h2>
          <p className="text-2xl">{stats.totalAssets}</p>
        </div>
        <div className="bg-white p-4 rounded shadow">
          <h2 className="text-lg font-semibold">Total Users</h2>
          <p className="text-2xl">{stats.totalUsers}</p>
        </div>
        <div className="bg-white p-4 rounded shadow">
          <h2 className="text-lg font-semibold">Assets by Category</h2>
          <ul>
            {stats.categoryWise.map(cat => (
              <li key={cat.category}>{cat.category}: {cat.count}</li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 
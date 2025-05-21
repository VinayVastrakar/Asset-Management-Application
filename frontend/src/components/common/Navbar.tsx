import React from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { Link } from "react-router-dom";

const Navbar: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

  return (
    <nav className="bg-primary text-white px-4 py-2 flex items-center">
      
      <Link to="/dashboard" className="mr-4 hover:underline">Dashboard</Link>
      <Link to="/assets" className="mr-4 hover:underline">Assets</Link>
      <Link to="/purchase-history" className="mr-4 hover:underline">Purchase History</Link>
      {user?.role === "Admin" && (
        <>
          <Link to="/users" className="mr-4 hover:underline">Users</Link>
          <Link to="/categories" className="mr-4 hover:underline">Categories</Link>
        </>
      )}
    </nav>
  );
};

export default Navbar; 
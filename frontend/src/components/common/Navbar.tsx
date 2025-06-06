import React from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { Link } from "react-router-dom";

const Navbar: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);

  const navItemClass = "relative group mr-4 cursor-pointer";
  const dropdownClass =
    "absolute left-0 mt-2 w-48 bg-white text-gray-800 rounded-md shadow-lg py-1 z-10 opacity-0 group-hover:opacity-100 invisible group-hover:visible transition-all duration-300";

  const linkClass = "block px-4 py-2 hover:bg-gray-100";

  return (
    <nav className="bg-primary text-white px-4 py-2 flex items-center">
      <Link to="/dashboard" className="mr-4 hover:underline">Dashboard</Link>
      

      {user?.role === "ADMIN" && (
        <>
          {/* User */}
          <div className={navItemClass}>
            <span className="hover:underline">User</span>
            <div className={dropdownClass}>
              <Link to="/users/add" className={linkClass}>Add New User</Link>
              <Link to="/users" className={linkClass}>Manage Users</Link>
            </div>
          </div>

          {/* Assets */}
          <div className={navItemClass}>
            <span className="hover:underline">Assets</span>
            <div className={dropdownClass}>
              <Link to="/assets/add" className={linkClass}>Add New Asset</Link>
              <Link to="/assets" className={linkClass}>View/Edit Asset</Link>
              
            </div>
          </div>

          {/* Purchase History */}
          <div className={navItemClass}>
            <span className="hover:underline">Purchase History</span>
            <div className={dropdownClass}>
              <Link to="/purchase-history/add" className={linkClass}>New Purchase</Link>
              <Link to="/purchase-history" className={linkClass}>View Purchases</Link>
              <Link to="/purchase-history/reports" className={linkClass}>Purchase Reports</Link>
            </div>
          </div>

          {/* Categories */}
          <div className={navItemClass}>
            <span className="hover:underline">Categories</span>
            <div className={dropdownClass}>
              <Link to="/categories/add" className={linkClass}>Add Category</Link>
              <Link to="/categories/manage" className={linkClass}>Manage Categories</Link>
            </div>
          </div>

          {/* Settings */}
          <div className={navItemClass}>
            <span className="hover:underline">Settings</span>
            <div className={dropdownClass}>
              <Link to="/settings/profile" className={linkClass}>Profile Settings</Link>
            </div>
          </div>
        </>
      )}
    </nav>
  );
};

export default Navbar;

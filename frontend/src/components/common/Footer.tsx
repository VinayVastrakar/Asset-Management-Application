import React from "react";

const Footer: React.FC = () => (
  <footer className="bg-primary text-white text-center py-4 mt-auto">
    <div>
      &copy; {new Date().getFullYear()} Your Company Name. All rights reserved.
    </div>
    <div className="text-sm">Asset Management System</div>
  </footer>
);

export default Footer; 
import React from "react";

interface Props {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<Props> = ({ currentPage, totalPages, onPageChange }) => (
  <div className="flex justify-center mt-4">
    {[...Array(totalPages)].map((_, idx) => (
      <button
        key={idx}
        className={`mx-1 px-3 py-1 rounded ${currentPage === idx + 1 ? "bg-primary text-white" : "bg-gray-200"}`}
        onClick={() => onPageChange(idx + 1)}
      >
        {idx + 1}
      </button>
    ))}
  </div>
);

export default Pagination; 
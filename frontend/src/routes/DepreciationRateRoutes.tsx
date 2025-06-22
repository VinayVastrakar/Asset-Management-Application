import React from 'react';
import { Route, Routes } from 'react-router-dom';
import ListDepreciationRates from '../components/depreciation/ListDepreciationRates';
import AddDepreciationRate from '../components/depreciation/AddDepreciationRate';
import EditDepreciationRate from '../components/depreciation/EditDepreciationRate';

const DepreciationRateRoutes: React.FC = () => (
  <Routes>
    <Route path="" element={<ListDepreciationRates />} />
    <Route path="add" element={<AddDepreciationRate />} />
    <Route path="edit/:id" element={<EditDepreciationRate />} />
  </Routes>
);

export default DepreciationRateRoutes; 
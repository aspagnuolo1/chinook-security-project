// src/components/CustomerItem.jsx
import React from 'react';

const CustomerItem = ({ customer }) => {
    return (
        <li>
            <h3>{customer.firstName} {customer.lastName}</h3>
            <p>Email: {customer.email}</p>
            <p>Country: {customer.country}</p>
        </li>
    );
};

export default CustomerItem;

import os

import requests
import streamlit as st


ORDER_API_URL = os.getenv(
    "ORDER_API_URL",
    "http://localhost:8081/api/orders",
)

st.set_page_config(page_title="Order Processing Dashboard", layout="wide")
st.title("Order Processing Dashboard")
st.button("Refresh orders")

try:
    response = requests.get(ORDER_API_URL, timeout=5)
    response.raise_for_status()
    orders = response.json()
except requests.RequestException as error:
    st.error(f"Could not load orders from Order Service: {error}")
    st.stop()

successful_orders = sum(order["status"] == "CONFIRMED" for order in orders)
failed_orders = sum(order["status"] == "FAILED" for order in orders)
processing_orders = sum(order["status"] == "CREATED" for order in orders)

total, successful, failed, processing = st.columns(4)
total.metric("Total orders", len(orders))
successful.metric("Successful orders", successful_orders)
failed.metric("Failed orders", failed_orders)
processing.metric("Being processed", processing_orders)

st.subheader("Orders")
st.dataframe(
    [
        {
            "Order ID": order["id"],
            "Customer ID": order["customerId"],
            "Amount": order["amount"],
            "Status": order["status"],
        }
        for order in orders
    ],
    use_container_width=True,
    hide_index=True,
)

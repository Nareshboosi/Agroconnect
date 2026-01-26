package com.agro.service;

public interface RefundService {

	void requestRefund(Long orderId, String name);

	void approveRefund(Long orderId, String name);

}

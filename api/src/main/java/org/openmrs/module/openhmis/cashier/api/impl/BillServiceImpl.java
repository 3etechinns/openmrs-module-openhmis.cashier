/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.openhmis.cashier.api.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.cashier.api.IBillService;
import org.openmrs.module.openhmis.cashier.api.ReceiptNumberGeneratorFactory;
import org.openmrs.module.openhmis.cashier.api.model.Bill;
import org.openmrs.module.openhmis.cashier.api.security.IDataAuthorizationPrivileges;
import org.openmrs.module.openhmis.cashier.api.util.CashierPrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BillServiceImpl
		extends BaseDataServiceImpl<Bill>
		implements IDataAuthorizationPrivileges, IBillService {
	@Override
	protected IDataAuthorizationPrivileges getPrivileges() {
		return this;
	}

	@Override
	protected void validate(Bill bill) throws APIException {
	}

	/**
	 * Saves the bill to the database, creating a new bill or updating an existing one.
	 *
	 * @param bill The bill to be saved.
	 * @return The saved bill.
	 * @should Generate a new receipt number if one has not been defined.
	 * @should Not generate a receipt number if one has already been defined.
	 * @should Throw APIException if receipt number cannot be generated.
	 */
	@Override
	@Authorized( { CashierPrivilegeConstants.MANAGE_BILLS } )
	@Transactional
	public Bill save(Bill bill) throws APIException {
		if (bill == null) {
			throw new NullPointerException("The bill must be defined.");
		}

		if (StringUtils.isEmpty(bill.getReceiptNumber())) {
			bill.setReceiptNumber(ReceiptNumberGeneratorFactory.getGenerator().generateNumber(bill));
		}

		return super.save(bill);
	}

	@Override
	@Authorized( { CashierPrivilegeConstants.VIEW_BILLS } )
	@Transactional(readOnly = true)
	public Bill getBillByReceiptNumber(String receiptNumber) throws APIException {
		if (StringUtils.isEmpty(receiptNumber)) {
			throw new IllegalArgumentException("The receipt number must be defined.");
		}
		if (receiptNumber.length() > 255) {
			throw new IllegalArgumentException("The receipt number must be less than 256 characters.");
		}

		Criteria criteria = dao.createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("receiptNumber", receiptNumber));

		return dao.selectSingle(getEntityClass(), criteria);
	}

	@Override
	public String getVoidPrivilege() {
		return CashierPrivilegeConstants.MANAGE_BILLS;
	}

	@Override
	public String getSavePrivilege() {
		return CashierPrivilegeConstants.MANAGE_BILLS;
	}

	@Override
	public String getPurgePrivilege() {
		return CashierPrivilegeConstants.PURGE_BILLS;
	}

	@Override
	public String getGetPrivilege() {
		return CashierPrivilegeConstants.VIEW_BILLS;
	}
}

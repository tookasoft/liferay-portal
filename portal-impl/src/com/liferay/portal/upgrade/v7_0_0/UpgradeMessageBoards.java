/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.portal.kernel.upgrade.BaseUpgradePortletPreferences;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

/**
 * @author Ivan Zaera
 */
public class UpgradeMessageBoards extends BaseUpgradePortletPreferences {

	protected String getEmailSignatureSeparator(
		PortletPreferences portletPreferences) {

		boolean emailHtmlFormat = PropsValues.MESSAGE_BOARDS_EMAIL_HTML_FORMAT;

		String emailHtmlFormatString = portletPreferences.getValue(
			"emailHtmlFormat", StringPool.BLANK);

		if (Validator.isNotNull(emailHtmlFormatString)) {
			emailHtmlFormat = GetterUtil.getBoolean(emailHtmlFormatString);
		}

		if (emailHtmlFormat) {
			return "<br />--<br />";
		}

		return "\n--\n";
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {PortletKeys.MESSAGE_BOARDS};
	}

	protected void upgradeEmailSignature(
			PortletPreferences portletPreferences,
			String emailMessageBodyPortletPreferencesKey,
			String emailMessageSignaturePortletPreferencesKey)
		throws ReadOnlyException {

		String emailMessageSignature = portletPreferences.getValue(
			emailMessageSignaturePortletPreferencesKey, StringPool.BLANK);

		if (Validator.isNotNull(emailMessageSignature)) {
			String emailMessageBody = portletPreferences.getValue(
				emailMessageBodyPortletPreferencesKey, StringPool.BLANK);

			String signatureSeparator = getEmailSignatureSeparator(
				portletPreferences);

			emailMessageBody += signatureSeparator + emailMessageSignature;

			portletPreferences.setValue(
				emailMessageBodyPortletPreferencesKey, emailMessageBody);
		}

		portletPreferences.reset(emailMessageSignaturePortletPreferencesKey);
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		upgradeEmailSignature(
			portletPreferences, "emailMessageAddedBody",
			"emailMessageAddedSignature");
		upgradeEmailSignature(
			portletPreferences, "emailMessageUpdatedBody",
			"emailMessageUpdatedSignature");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}
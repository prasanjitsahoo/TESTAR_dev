/**
 * Copyright (c) 2021 - 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2021 - 2023 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.testar.monkey.alayer.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class WdRemoteScrollTypeAction extends WdRemoteTypeAction {
	private static final long serialVersionUID = 5967321502092097275L;

	public WdRemoteScrollTypeAction(WdWidget widget, CharSequence keys) {
		super(widget, keys);
		this.set(Tags.Desc, "Remote scroll and type " + keys + " to widget " 
				+ widget.element.getElementDescription() + " : " + widget.element.remoteWebElement.getId());
		this.set(Tags.Role, WdActionRoles.RemoteScrollType);
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		try {
			RemoteWebElement remoteElement = widget.element.remoteWebElement;
			RemoteWebDriver d = (RemoteWebDriver)remoteElement.getWrappedDriver();
			// Scroll the element to the middle of the screen
			// Because scrolling the element to the top may provoke an obscuration by some web headers
			d.executeScript("arguments[0].scrollIntoView({block: 'center'})", remoteElement);
			remoteElement.clear();
			org.testar.monkey.Util.pause(0.1);
			remoteElement.sendKeys(keys);
		}
		catch (ElementClickInterceptedException ie) {
			// This happens when other element obscure the desired element to interact with
			logger.warn(String.format("%s : %s", this.get(Tags.Desc, ""), ie.getMessage()));
		}
		catch (StaleElementReferenceException se) {
			// This happens when the state changes between obtaining the widget and executing the action
			logger.warn(String.format("%s : %s", this.get(Tags.Desc, ""), se.getMessage()));
		}
		catch (Exception e) {
			logger.warn("Remote scroll and type action failed", e);
		}
	}

	@Override
	public String toShortString() {
		return "Remote scroll and type " + keys + " " + widget.element.getElementDescription();
	}

}

/***************************************************************************************************
*
* Copyright (c) 2013 - 2023 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
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
*******************************************************************************************************/

package org.testar.protocols;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Set;

import org.testar.SutVisualization;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.DefaultProtocol;

import org.testar.managers.DataManager;
import org.testar.managers.FilteringManager;

/**
 * The ClickFilterLsyerProtocol adds the functionality to filter actions in SPY mode by
 * pressing CAPS-LOCK + SHIFT and clicking on the widget. 
 */
public class ClickFilterLayerProtocol extends DefaultProtocol {

    private boolean preciseCoding = false; // false =>  CodingManager.ABSTRACT_R_T_ID; true => CodingManager.ABSTRACT_R_T_P_ID
    private boolean displayWhiteTabu = false;
    private boolean whiteTabuMode = false; // true => white, false = tabu
    private boolean shiftPressed = false;

    private double mouseX = Double.MIN_VALUE;
    private double mouseY = Double.MIN_VALUE;
    private double[] filterArea = new double[]{Double.MAX_VALUE,Double.MAX_VALUE,Double.MIN_VALUE,Double.MIN_VALUE}; // <x1,y1,x2,y2>
    
    private FilteringManager filteringManager;
    private DataManager dataManager;
    
    /**
     * Constructor.
     */
	public ClickFilterLayerProtocol(){
		super();
		filteringManager = new FilteringManager();
		dataManager = new DataManager();
		filteringManager.loadFilters();
		dataManager.loadInputValues();
		// If the environment is not headless, initialize the CAPS LOCK display mouse
		if (!GraphicsEnvironment.isHeadless()) {
			displayWhiteTabu = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		}
	}

	/**
	 * Add additional TESTAR keyboard shortcuts in SPY mode to enable the filtering of actions by clicking on them
	 * @param key
	 */
    @Override
    public void keyDown(KBKeys key) {    	
        super.keyDown(key);        
        if (mode() == Modes.Spy){ 
        	if (key == KBKeys.VK_CAPS_LOCK)
        		displayWhiteTabu = !displayWhiteTabu;
        	else if (key == KBKeys.VK_TAB)
        		preciseCoding = !preciseCoding;
        	else if (key == KBKeys.VK_SHIFT)
        		shiftPressed = true;
	    	else if (key == KBKeys.VK_CONTROL){
	    		filterArea[0] = mouseX;
	    		filterArea[1] = mouseY;
	    	}
        }
    }

    @Override
    public void keyUp(KBKeys key) {    	
    	super.keyUp(key);
        if (mode() == Modes.Spy){
        	if (key == KBKeys.VK_SHIFT) {
	    		shiftPressed = false;
        	} else if (key == KBKeys.VK_CONTROL && displayWhiteTabu){
	    		filterArea[2] = mouseX;
	    		filterArea[3] = mouseY;
	    		whiteTabuMode = shiftPressed;
	    		filteringManager.manageWhiteTabuLists(getStateForClickFilterLayerProtocol(),this.mouse,this.filterArea,this.whiteTabuMode,this.preciseCoding);
	    	}
        }
    }
    	
	@Override
	public void mouseMoved(double x, double y) {
		mouseX = x;
		mouseY = y;
	}

    @Override
	protected void visualizeActions(Canvas canvas, State state, Set<Action> actions){
		SutVisualization.visualizeActions(canvas, state, actions);
    	if(displayWhiteTabu && (mode() == Modes.Spy)) {
    		filteringManager.visualizeActions(canvas,state);
    	}
	}

    protected boolean blackListed(Widget w){
    	return filteringManager.blackListed(w);
    }

    protected boolean whiteListed(Widget w){
    	return filteringManager.whiteListed(w);
    }

    //TODO why is filteringManager having random text functions? also, the original one is in DefaultProtocol and this is the only usage?
    @Override
    protected String getRandomText(Widget w){
    	String randomText = filteringManager.getRandomText(w);
    	if (randomText == null || randomText.length() == 0)
    		return super.getRandomText(w);
    	else
    		return randomText;
    }
}

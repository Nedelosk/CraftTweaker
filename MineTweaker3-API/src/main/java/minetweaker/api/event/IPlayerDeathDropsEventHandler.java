/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.api.event;

import stanhebben.zenscript.annotations.ZenClass;

/**
 *
 * @author Stan
 */
@ZenClass("minetweaker.event.IPlayerDeathDropsEventHandler")
public interface IPlayerDeathDropsEventHandler {
	public void handle(PlayerDeathDropsEvent event);
}
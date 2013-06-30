package com.slamdunk.quester.display.hud.actionslots;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.slamdunk.quester.display.actors.ActionSlotActor;
import com.slamdunk.quester.display.actors.PathMarkerActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

public class ActionSlots {
	
	private final float cellHeight;
	private final float cellWidth;
	private final ActionSlotActor dragActor;
	
	private final DragAndDrop dragAndDrop;

	private final List<ActionSlotActor> arrivalSlots;
	private final List<ActionSlotActor> stockSlots;
	private final List<ActionSlotActor> upcomingSlots;
	
	private final List<Target> pathMarkers;

	public ActionSlots() {
		upcomingSlots = new ArrayList<ActionSlotActor>();
		arrivalSlots = new ArrayList<ActionSlotActor>();
		stockSlots = new ArrayList<ActionSlotActor>();
		pathMarkers = new ArrayList<Target>();
		
		cellWidth = GameControler.instance.getScreen().getMap().getCellWidth();
		cellHeight = GameControler.instance.getScreen().getMap().getCellHeight();
		dragAndDrop = new DragAndDrop();
		dragAndDrop.setDragActorPosition(- cellWidth / 2, cellHeight / 2);
		
		// On crée l'acteur qui nous servira pendant les drags. Inutile d'en créer un différent
		// à chaque fois, on réutilisera le même.
		dragActor = ActionSlotsHelper.createEmptySlot();
	}
	
	public void act(float delta) {
		for (ActionSlotActor slot : upcomingSlots) {
			slot.act(delta);
		}
	}
	
	public void addArrivalSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			arrivalSlots.add(slot);
			addSource(slot);
		}
	}

	public void addSource(final ActionSlotActor source) {
		dragAndDrop.addSource(new Source(source) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				if (source.getControler().getData().action == QuesterActions.NONE) {
					return null;
				}
				
				Payload payload = new Payload();
				payload.setObject(source.getControler());

				// On crée un dragActor correspondant à ce que contient la source
				ActionSlotsHelper.copySlot(source, dragActor);
				dragActor.setSize(cellWidth, cellHeight);
				payload.setDragActor(dragActor);
				// On modifie l'image source pour afficher un slot vide
				ActionSlotsHelper.setSlotData(ActionSlotsHelper.EMPTY_SLOT, source);

				return payload;
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Target target) {
				super.dragStop(event, x, y, pointer, target);
				
				// Fin du drag and drop. Si pas lâché sur une cible valide
				// on replace l'action d'origine dans le slot
				if (target == null) {
					ActionSlotsHelper.copySlot((ActionSlotActor)dragAndDrop.getDragActor(), source);
				}
			}
		});
	}

	public void addStockSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			stockSlots.add(slot);
			addSource(slot);
			addTarget(slot);
		}
	}

	public Target addTarget(final WorldElementActor actor) {
		Target target = new Target(actor) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				actor.getControler().onDropHoverEnter(dragActor.getControler().getData().action);
				return true;
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				WorldElementControler controler = actor.getControler();
				ActionSlotControler slotControler = dragActor.getControler();
				if (controler.canAcceptDrop(slotControler.getData().action)) {
					// On laisse le contrôleur gérer l'action
					actor.getControler().receiveDrop(slotControler);
					// Et on met à jour les slots
					fillActionSlots();
				} else {
					// Si le chargement a été refusé, on replace l'action d'origine dans le slot
					ActionSlotsHelper.copySlot((ActionSlotActor)dragAndDrop.getDragActor(), (ActionSlotActor)source.getActor());
				}
			}

			public void reset (Source source, Payload payload) {
				actor.getControler().onDropHoverLeave(dragActor.getControler().getData().action);
			}
		};
		dragAndDrop.addTarget(target);
		return target;
	}
	
	public void addUpcomingSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			upcomingSlots.add(slot);
		}
	}
	
	public void fillActionSlots() {
		// Remplit chaque upcomingSlot
		for (ActionSlotActor slot : upcomingSlots) {
			if (slot.getControler().getData().action == QuesterActions.NONE) {
				ActionSlotsHelper.fillActionSlot(slot);
			}
		}
		
		// Fait descendre les actions pour remplir les arrivalSlots, en commençant par le dernier
		for (int curArrivalSlot = arrivalSlots.size() - 1; curArrivalSlot >= 0; curArrivalSlot --) {
			ActionSlotActor arrivalSlot = arrivalSlots.get(curArrivalSlot);
			if (arrivalSlot.getControler().getData().action == QuesterActions.NONE) {
				// Récupère le dernier upcomingSlot
				ActionSlotActor upcomingSlot = upcomingSlots.get(upcomingSlots.size() - 1);
				// Affecte ses données au slot d'arrivée vide
				upcomingSlot.affectTo(arrivalSlot);
				// Fait avancer tous les upcomings d'un cran
				shiftUpcomings();
			}
		}
	}

	/**
	 * Fait avancer tous les upcomings d'un cran, puis remplit le premier de la liste
	 * (vide après le décalage) avec le prochain élément prévu.
	 */
	private void shiftUpcomings() {
		ActionSlotActor curUpcoming;
		ActionSlotActor previousUpcoming;
		for (int curIdx = upcomingSlots.size() - 1; curIdx > 0; curIdx--) {
			curUpcoming = upcomingSlots.get(curIdx);
			previousUpcoming = upcomingSlots.get(curIdx - 1);
			
			previousUpcoming.affectTo(curUpcoming);
		}
		// Remplit le slot vide, qui est forcément le premier
		ActionSlotsHelper.fillActionSlot(upcomingSlots.get(0));
	}

	public boolean isDragging() {
		return dragAndDrop.isDragging();
	}

	public void addPathMarker(PathMarkerActor actor) {
		Target target = addTarget(actor);
		pathMarkers.add(target);
	}
	
	public void clearPathMarkers() {
		for (Target target : pathMarkers) {
			dragAndDrop.removeTarget(target);
		}
		pathMarkers.clear();
	}
}

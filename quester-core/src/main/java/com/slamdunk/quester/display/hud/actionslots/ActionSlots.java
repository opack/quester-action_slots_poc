package com.slamdunk.quester.display.hud.actionslots;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.slamdunk.quester.display.actors.ActionSlotActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

public class ActionSlots {
	private final List<ActionSlotActor> arrivalSlots;
	private final float cellHeight;
	private final float cellWidth;
	private final ActionSlotActor dragActor;
	
	private final DragAndDrop dragAndDrop;

	private final List<ActionSlotActor> stockSlots;

	private final List<ActionSlotActor> upcomingSlots;

	public ActionSlots() {
		upcomingSlots = new ArrayList<ActionSlotActor>();
		arrivalSlots = new ArrayList<ActionSlotActor>();
		stockSlots = new ArrayList<ActionSlotActor>();
		
		cellWidth = GameControler.instance.getScreen().getMap().getCellWidth();
		cellHeight = GameControler.instance.getScreen().getMap().getCellHeight();
		dragAndDrop = new DragAndDrop();
		dragAndDrop.setDragActorPosition(- cellWidth / 2, cellHeight / 2);
		
		// On cr�e l'acteur qui nous servira pendant les drags. Inutile d'en cr�er un diff�rent
		// � chaque fois, on r�utilisera le m�me.
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
				Payload payload = new Payload();
				payload.setObject(source.getControler());

				// On cr�e un dragActor correspondant � ce que contient la source
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
				
				// Fin du drag and drop. Si pas l�ch� sur une cible valide
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

	public void addTarget(final WorldElementActor target) {
		dragAndDrop.addTarget(new Target(target) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				Image targetImage = target.getImage();
				if (targetImage != null) {
					if (target.getControler().canAcceptDrop(payload)) {
						targetImage.setColor(Color.GREEN);
					} else {
						targetImage.setColor(Color.RED);
					}
				}
				return true;
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				WorldElementControler controler = target.getControler();
				if (controler.canAcceptDrop(payload)) {
					// On laisse le contr�leur g�rer l'action
					target.getControler().receiveDrop(dragActor.getControler());
					// Et on met � jour les slots
					fillActionSlots();
				} else {
					// Si le chargement a �t� refus�, on replace l'action d'origine dans le slot
					ActionSlotsHelper.copySlot((ActionSlotActor)dragAndDrop.getDragActor(), (ActionSlotActor)source.getActor());
				}
			}

			public void reset (Source source, Payload payload) {
				Image targetImage = target.getImage();
				if (targetImage != null) {
					targetImage.setColor(Color.WHITE);
				}
			}
		});
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
		
		// Fait descendre les actions pour remplir les arrivalSlots, en commen�ant par le dernier
		for (int curArrivalSlot = arrivalSlots.size() - 1; curArrivalSlot >= 0; curArrivalSlot --) {
			ActionSlotActor arrivalSlot = arrivalSlots.get(curArrivalSlot);
			if (arrivalSlot.getControler().getData().action == QuesterActions.NONE) {
				// R�cup�re le dernier upcomingSlot
				ActionSlotActor upcomingSlot = upcomingSlots.get(upcomingSlots.size() - 1);
				System.out.println("ActionSlots.fillActionSlots() AVANT");
				printSlotActions(upcomingSlots);
				// Affecte ses donn�es au slot d'arriv�e vide
				upcomingSlot.affectTo(arrivalSlot);
				// Fait avancer tous les upcomings d'un cran
				shiftUpcomings();
			}
		}
	}

	/**
	 * Fait avancer tous les upcomings d'un cran, puis remplit le premier de la liste
	 * (vide apr�s le d�calage) avec le prochain �l�ment pr�vu.
	 */
	private void shiftUpcomings() {
		ActionSlotActor curUpcoming;
		ActionSlotActor previousUpcoming;
		for (int curIdx = upcomingSlots.size() - 1; curIdx > 0; curIdx--) {
			curUpcoming = upcomingSlots.get(curIdx);
			previousUpcoming = upcomingSlots.get(curIdx - 1);
			
			System.out.println("ActionSlots.shiftUpcomings() DEB");
			previousUpcoming.affectTo(curUpcoming);
			System.out.println("ActionSlots.shiftUpcomings() FIN");
		}
		// Remplit le slot vide, qui est forc�ment le premier
		ActionSlotsHelper.fillActionSlot(upcomingSlots.get(0));
		
		System.out.println("ActionSlots.fillActionSlots() APRES");
		printSlotActions(upcomingSlots);
		System.out.println("ActionSlots.shiftUpcomings() --------------");
	}

	private void printSlotActions(List<ActionSlotActor> upcomingSlots2) {
		for (ActionSlotActor slot : upcomingSlots) {
				System.out.println("ActionSlots.printSlotActions() " + slot.getControler().getData().action);
		}
	}

	public boolean isDragging() {
		return dragAndDrop.isDragging();
	}
}

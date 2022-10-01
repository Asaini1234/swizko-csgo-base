package rat.swizko.ui.uiHelpers.tables

import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSelectBox
import com.kotcrab.vis.ui.widget.VisTable
import rat.swizko.curLocale
import rat.swizko.curSettings
import rat.swizko.dbg
import rat.swizko.toLocale
import rat.swizko.ui.changed
import rat.swizko.ui.tabs.categorySelected
import rat.swizko.ui.tabs.gunCategories
import rat.swizko.ui.uiHelpers.VisCheckBoxCustom
import rat.swizko.ui.uiHelpers.VisInputFieldCustom
import rat.swizko.ui.uiHelpers.aimTab.ATabVisCheckBox
import rat.swizko.ui.uiHelpers.aimTab.ATabVisSlider
import rat.swizko.ui.uiPanels.aimTab
import rat.swizko.ui.uiUpdate

class AimBacktrackTable: VisTable(false) {
    //Init labels/sliders/boxes that show values here

    val enableBacktrack = VisCheckBoxCustom("Master Switch", "ENABLE_BACKTRACK")
    val backtrackVisualize = VisCheckBoxCustom("Visualize", "BACKTRACK_VISUALIZE")
    val backtrackEnableKey = VisCheckBoxCustom("Enable On Key", "ENABLE_BACKTRACK_ON_KEY")
    val backtrackKey = VisInputFieldCustom("Backtrack Key", "BACKTRACK_KEY")
    val backtrackSpotted = VisCheckBoxCustom("Check Spotted", "BACKTRACK_SPOTTED")
    val backtrackWeaponEnabled = ATabVisCheckBox("Enable Weapon Backtrack", "_BACKTRACK")
    val backtrackMS = ATabVisSlider("Backtrack MS", "_BACKTRACK_MS", 20f, 200f, 5f, true, width1 = 200F, width2 = 250F)

    //Override Weapon Checkbox & Selection Box
    private val categorySelection = VisTable()
    val categorySelectionBox = VisSelectBox<String>()
    val categorySelectLabel = VisLabel("${"Weapon-Category".toLocale()}:")

    init {
        //Create Category Selector Box
        val itemsArray = Array<String>()
        for (i in gunCategories) {
            if (dbg && curLocale[i].isBlank()) {
                println("[DEBUG] ${curSettings["CURRENT_LOCALE"]} $i is missing!")
            }

            itemsArray.add(curLocale[i])
        }

        categorySelectionBox.items = itemsArray
        categorySelectionBox.selectedIndex = 0

        categorySelected = gunCategories[categorySelectionBox.selectedIndex]
        categorySelection.add(categorySelectLabel).padRight(200F-categorySelectLabel.width)
        categorySelection.add(categorySelectionBox)

        categorySelectionBox.changed { _, _ ->
            categorySelected = gunCategories[categorySelectionBox.selectedIndex]
            aimTab.tAim.categorySelectionBox.selectedIndex = gunCategories.indexOf(categorySelected)
            aimTab.tBacktrack.categorySelectionBox.selectedIndex = gunCategories.indexOf(categorySelected)
            uiUpdate()
            true
        }

        //Default menu size width is 565
        //Default menu size heght is 535
        //Texts are 200
        //Sliders are 250
        //Leaves 25 for left and right side to center
        apply {
            padLeft(25F)
            padRight(25F)
            //Add all items to label for tabbed pane content
            add(enableBacktrack).left().row()
            add(backtrackVisualize).left().row()
            add(backtrackEnableKey).left().row()
            add(backtrackKey).left().row()

            add(backtrackSpotted).left().row()

            add(categorySelection).left().row()
            add(backtrackWeaponEnabled).left().row()
            add(backtrackMS).left().row()

            addSeparator()
        }
    }
}
/*
 *  NachoCalendar
 *
 * Project Info:  http://nachocalendar.sf.net
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Changes
 * -------
 *
 * -------
 *
 * DateFieldSetter.java
 *
 * Created on Dec 17, 2005
 */
package net.sf.nachocalendar.customizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.extern.slf4j.Slf4j;
import net.sf.nachocalendar.components.CalendarPanel;
import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.components.DatePanel;
import net.sf.nachocalendar.components.DayRenderer;
import net.sf.nachocalendar.components.HeaderRenderer;
import net.sf.nachocalendar.model.DataModel;
import net.sf.nachocalendar.model.DateSelectionModel;

/**
 * Class that sets properties of components
 * using direct calls to methods.
 * 
 * @author Ignacio Merani
 *
 * 
 */
@Slf4j
public class DirectSetter implements PropertiesSetter {
    private boolean initialized;
    private boolean allowsInvalid, printMoon;
    private boolean showOkCancel, showToday, antiAliased;
    private int firstDayOfWeek = Calendar.SUNDAY;
    private Class headerRenderer, model, renderer;
    private String todayCaption;
    private boolean[] workingDays;
    private int selectionMode = DateSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    private int orientation = CalendarPanel.HORIZONTAL;
    private int scrollPosition = CalendarPanel.LEFT;
    private int yearPosition = CalendarPanel.UP;
    private String dateFormat;

    /**
     * Constructor with a Customizer.
     * @param customizer
     */
    public DirectSetter(final Customizer customizer) {
        init(customizer);
    }

    /**
     * Default constructor, must add a
     * Customizer via setter.
     *
     */
    public DirectSetter() {
    }

    /**
     * Sets the current customizer.
     * 
     * @param customizer
     */
    public void setCustomizer(final Customizer customizer) {
        init(customizer);
    }

    private void init(final Customizer customizer) {
        final String first = customizer.getString("firstDayOfWeek");
        if (first != null) {
            if (first.toLowerCase().equals("monday")) {
                this.firstDayOfWeek = Calendar.MONDAY;
            }
        }

        allowsInvalid = customizer.getBoolean("allowsInvalid");

        antiAliased = customizer.getBoolean("antiAliased");

        final String dateFormat = customizer.getString("dateFormat");
        if (dateFormat != null) {
            this.dateFormat = dateFormat;
        }

        final String headerRenderer = customizer.getString("headerRenderer");
        if (headerRenderer != null) {
            this.headerRenderer = loadClass(headerRenderer);
        }

        final String model = customizer.getString("model");
        if (model != null) {
            this.model = loadClass(model);
        }

        printMoon = customizer.getBoolean("printMoon");

        final String renderer = customizer.getString("renderer");

        if (renderer != null) {
            this.renderer = loadClass(renderer);
        }

        showOkCancel = customizer.getBoolean("showOkCancel");
        showToday = customizer.getBoolean("showToday");
        todayCaption = customizer.getString("todayCaption");

        final String workingDays = customizer.getString("workingDays");
        if (workingDays != null) {
            final String[] work = workingDays.split(",");
            final boolean[] wd = new boolean[7];
            for (int i = 0; i < work.length; i++) {
                if (i == 7) {
                    break;
                }
                wd[i] = Boolean.valueOf(work[i]).booleanValue();
            }
            this.workingDays = wd;
        }

        final String selection = customizer.getString("selectionMode");
        if (selection != null) {
            if (selection.toLowerCase().equals("singleinterval")) {
                selectionMode = DateSelectionModel.SINGLE_INTERVAL_SELECTION;
            }
            if (selection.toLowerCase().equals("singleselection")) {
                selectionMode = DateSelectionModel.SINGLE_SELECTION;
            }
        }

        final String orientation = customizer.getString("orientation");
        if (orientation != null) {
            if (orientation.toLowerCase().equals("vertical")) {
                this.orientation = CalendarPanel.VERTICAL;
            }
        }

        final String scrollposition = customizer.getString("scrollPosition");
        if (scrollposition != null) {
            if (scrollposition.toLowerCase().equals("right")) {
                this.scrollPosition = CalendarPanel.RIGHT;
            }
        }

        final String yearposition = customizer.getString("yearPosition");
        if (yearposition != null) {
            if (yearposition.toLowerCase().equals("down")) {
                this.yearPosition = CalendarPanel.DOWN;
            }
        }
        initialized = true;
    }

    private Class loadClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException e) {
            log.error("Class issue", e);
            return null;
        }
    }

    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.PropertiesSetter#customize(net.sf.nachocalendar.components.DateField)
     */
    @Override
    public void customize(final DateField datefield) {
        if (!initialized) {
            throw new IllegalStateException("This setter is not initialized.");
        }
        datefield.setFirstDayOfWeek(firstDayOfWeek);

        datefield.setAllowsInvalid(allowsInvalid);

        datefield.setAntiAliased(antiAliased);

        if (dateFormat != null) {
            datefield.setDateFormat(new SimpleDateFormat(dateFormat));
        }

        if (headerRenderer != null) {
            try {
                datefield.setHeaderRenderer((HeaderRenderer) headerRenderer.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("Header renderer", e);
            }
        }

        if (model != null) {
            try {
                datefield.setModel((DataModel) model.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("Model", e);
            }
        }

        datefield.setPrintMoon(printMoon);

        if (renderer != null) {
            try {
                datefield.setRenderer((DayRenderer) renderer.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("renderer", e);
            }
        }

        datefield.setShowOkCancel(showOkCancel);
        datefield.setShowToday(showToday);
        if (todayCaption != null) {
            datefield.setTodayCaption(todayCaption);
        }

        if (workingDays != null) {
            datefield.setWorkingDays(workingDays);
        }

    }

    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.PropertiesSetter#customize(net.sf.nachocalendar.components.CalendarPanel)
     */
    @Override
    public void customize(final CalendarPanel calendarpanel) {
        if (!initialized) {
            throw new IllegalStateException("This setter is not initialized.");
        }
        calendarpanel.setFirstDayOfWeek(firstDayOfWeek);

        calendarpanel.setAntiAliased(antiAliased);
        calendarpanel.setSelectionMode(selectionMode);
        if (headerRenderer != null) {
            try {
                calendarpanel.setHeaderRenderer((HeaderRenderer) headerRenderer.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("Header renderer", e);
            }
        }

        if (model != null) {
            try {
                calendarpanel.setModel((DataModel) model.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("Model", e);
            }
        }

        calendarpanel.setPrintMoon(printMoon);

        if (renderer != null) {
            try {
                calendarpanel.setRenderer((DayRenderer) renderer.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("renderer", e);
            }
        }

        calendarpanel.setShowToday(showToday);
        if (todayCaption != null) {
            calendarpanel.setTodayCaption(todayCaption);
        }

        if (workingDays != null) {
            calendarpanel.setWorkingdays(workingDays);
        }

        calendarpanel.setOrientation(orientation);
        calendarpanel.setScrollPosition(scrollPosition);
        calendarpanel.setYearPosition(yearPosition);
    }

    /* (non-Javadoc)
     * @see net.sf.nachocalendar.customizer.PropertiesSetter#customize(net.sf.nachocalendar.components.DatePanel)
     */
    @Override
    public void customize(final DatePanel datepanel) {
        if (!initialized) {
            throw new IllegalStateException("This setter is not initialized.");
        }
        datepanel.setFirstDayOfWeek(firstDayOfWeek);

        datepanel.setAntiAliased(antiAliased);
        datepanel.setSelectionMode(selectionMode);
        if (headerRenderer != null) {
            try {
                datepanel.setHeaderRenderer((HeaderRenderer) headerRenderer.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("Header renderer", e);
            }
        }

        if (model != null) {
            try {
                datepanel.setModel((DataModel) model.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("Model", e);
            }
        }

        datepanel.setPrintMoon(printMoon);

        if (renderer != null) {
            try {
                datepanel.setRenderer((DayRenderer) renderer.newInstance());
            } catch (final InstantiationException | IllegalAccessException e) {
                log.error("Renderer", e);
            }
        }

        datepanel.setShowToday(showToday);
        if (todayCaption != null) {
            datepanel.setTodayCaption(todayCaption);
        }

        if (workingDays != null) {
            datepanel.setWorkingDays(workingDays);
        }
    }
}

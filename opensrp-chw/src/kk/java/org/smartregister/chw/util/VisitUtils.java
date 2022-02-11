package org.smartregister.chw.util;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.utils.CoreConstants;

public class VisitUtils {
    public static boolean isFirstVisit(final MemberObject memberObject, LocalDate lmp) {
        int gaWeeks = Days.daysBetween(lmp, new LocalDate()).getDays() / 7;
        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), CoreConstants.EventType.ANC_HOME_VISIT);
        // Assumption 6 months pregnancy is 24 weeks GA
        return gaWeeks < 24 && lastVisit == null;
    }
}

package ru.meetingbot.db;

import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.state.admin.BanUserState;
import ru.meetingbot.chat.state.admin.FreezeUserState;
import ru.meetingbot.chat.state.admin.SendMessageAllUsersState;
import ru.meetingbot.chat.state.admin.UnfreezeUserState;
import ru.meetingbot.chat.state.main.MainState;
import ru.meetingbot.chat.state.StartState;
import ru.meetingbot.chat.state.main.UpdateProfileState;
import ru.meetingbot.chat.state.main.updateprofile.*;
import ru.meetingbot.chat.state.questionnaire.*;
import ru.meetingbot.chat.state.week.monday.MondayState;
import ru.meetingbot.chat.state.week.sunday.one.SundayPartOneAllState;
import ru.meetingbot.chat.state.week.sunday.one.SundayPartOneHowWasMeetingState;
import ru.meetingbot.chat.state.week.sunday.one.SundayPartOneTellUsMoreState;
import ru.meetingbot.chat.state.week.sunday.two.SundayPartTwoTellUsMoreState;
import ru.meetingbot.chat.state.week.sunday.two.SundayPartTwoWhoOtherState;
import ru.meetingbot.chat.state.week.sunday.two.SundayPartTwoWhoNotHavePartnerState;
import ru.meetingbot.chat.state.week.wednesday.WednesdayPartOneWhoHavePairState;

public enum ChatState {
    // админские состояния
    SEND_MESSAGE_ALL_USERS(555, new SendMessageAllUsersState()),
    BAN_USER(556, new BanUserState()),
    FREEZE_USER(557, new FreezeUserState()),
    UNFREEZE_USER(558, new UnfreezeUserState()),

    // пользовательские состояния
    START( 1, new StartState()),
        WRITE_FULL_NAME(2, new WriteFullNameState()),
        WRITE_PROFILE_LINK(3, new WriteProfileLinkState()),
        WRITE_JOB(4, new WriteJobState()),
        WRITE_HOBBIE(5, new WriteHobbieState()),
        WRITE_LOCATION(22, new WriteLocationState()),
        WRITE_YEARS_OF_EXPERIENCE(6, new WriteYearsOfExperienceState()),

    MAIN(7, new MainState()),
        UPDATE_PROFILE(8, new UpdateProfileState()),
            UPDATE_FULL_NAME(9, new UpdateFullNameState()),
            UPDATE_PROFILE_LINK(10, new UpdateProfileLinkState()),
            UPDATE_JOB(11, new UpdateJobState()),
            UPDATE_HOBBIE(12, new UpdateHobbieState()),
            UPDATE_AGE(13, new UpdateYearsOfExperienceState()),

    S_TWO_WHO_NOT_HAVE_PARTNER(14, new SundayPartTwoWhoNotHavePartnerState()),
    S_TWO_WHO_OTHER(15, new SundayPartTwoWhoOtherState()),

    S_TWO_TELL_US_MORE(16, new SundayPartTwoTellUsMoreState()),

    S_ONE_ALL(17, new SundayPartOneAllState()),
        S_ONE_TELL_US_MORE(18, new SundayPartOneTellUsMoreState()),
    S_ONE_HOW_WAS_MEETING(19, new SundayPartOneHowWasMeetingState()),

    MONDAY(20, new MondayState()),

    W_ONE_WHO_HAVE_PAIR(21, new WednesdayPartOneWhoHavePairState());

    /**
     * chatStateId - значение в базе данных
     */
    private final short chatStateId;
    private final BaseChatState chatState;

    ChatState(int chatStateId, BaseChatState chatState) {
        this.chatStateId = (short) chatStateId;
        this.chatState = chatState;
    }

    public static ChatState stateFromId(int chatStateId) {
        for (ChatState cs : ChatState.values()) {
            if (cs.id() == chatStateId) {
                return cs;
            }
        }
        throw new RuntimeException();
    }

    public short id() {
        return chatStateId;
    }

    public BaseChatState value() {
        return chatState;
    }
}

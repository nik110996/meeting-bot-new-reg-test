package ru.meetingbot.deamon.day;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.meetingbot.ResBundle;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.FinalMeetingState;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.deamon.algorithm.Algorithm;
import ru.meetingbot.chat.ChatWithState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.DBConst;
import ru.meetingbot.db.dao.ChatDAO;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.db.model.MeetingModel;
import ru.meetingbot.db.model.UserModel;
import ru.meetingbot.util.StringMarkdownV2;
import ru.meetingbot.util.UsersUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Monday {

    private String getMessageWithProfile(UserModel userModel) {
        String userName = StringMarkdownV2.getString(userModel.getUserName());
        String fullName = StringMarkdownV2.getString(userModel.getFullName());
        String profileLink = StringMarkdownV2.getString(userModel.getProfileLink());
        String instagram = "[" + profileLink + "]" + "(https://www.instagram.com/" + profileLink + "/)";
        String job = StringMarkdownV2.getString(userModel.getJob());
        String hobbie = StringMarkdownV2.getString(userModel.getHobbie());
        Short age = userModel.getAge();

        String reachOut = "__[Telegram]" + "(https://t.me/" + userName + ")__";

        String separator = ResBundle.getMessage("writeAgeState.congratulation.separator");
        String text = new StringBuilder()
                .append(ResBundle.getMessage("monday.beforeUsername")).append(userName).append(ResBundle.getMessage("monday.afterUsername"))
                .append(ResBundle.getMessage("writeAgeState.congratulation.name")).append(separator).append(fullName).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.profileLink")).append(separator).append(instagram).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.job")).append(separator).append(job).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.hobbie")).append(separator).append(hobbie).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.age")).append(separator).append(age).append("\n")
                .append(ResBundle.getMessage("monday.beforeReachOut")).append(reachOut).append(ResBundle.getMessage("monday.afterReachOut"))

                .toString();

        return text;
    }

    private void skipWhoNotHavePartner() {
        List<ChatWithState> list = new ChatDAO().getWhere(true, DBConst.T_CHAT_C_CHAT_STATE_ID, ChatState.S_TWO_WHO_NOT_HAVE_PARTNER.id());

        /* убрать из листа тех, кто забанен */
        list.removeIf(chatWithState -> UsersUtils.isBanned(chatWithState.getUserId()));

        MeetingDAO meetingDAO = new MeetingDAO();
        FinalMeetingDAO finalMeetingDAO = new FinalMeetingDAO();
        for (ChatWithState chat : list) {
            long userId = chat.getUserId();
            /* удалить из meeting */
            meetingDAO.deleteById(userId);
            /* добавить в final_meeting пропускает эту неделю */
            finalMeetingDAO.create(new FinalMeetingModel(userId, null, FinalMeetingState.M_SKIP_WEEK.id(), null, LocalDate.now()));

            ChatWork.changeChatState(userId, ChatState.MONDAY);
        }
    }

    private void skipWhoOther() {
        List<ChatWithState> list = new ChatDAO().getWhere(true, DBConst.T_CHAT_C_CHAT_STATE_ID, ChatState.S_TWO_WHO_OTHER.id());

        /* убрать из листа тех, кто забанен */
        list.removeIf(chatWithState -> UsersUtils.isBanned(chatWithState.getUserId()));

        FinalMeetingDAO finalMeetingDAO = new FinalMeetingDAO();
        for (ChatWithState chat : list) {
            long userId = chat.getUserId();

            /* добавить в final_meeting пропускает эту неделю */
            finalMeetingDAO.create(new FinalMeetingModel(userId, null, FinalMeetingState.M_SKIP_WEEK.id(), null, LocalDate.now()));

            ChatWork.changeChatState(userId, ChatState.MONDAY);
        }
    }

    private void getPairs() {
        Algorithm algorithm = new Algorithm();

        UserDAO userDAO = new UserDAO();
        MeetingDAO meetingDAO = new MeetingDAO();
        FinalMeetingDAO finalMeetingDAO = new FinalMeetingDAO();
        List<MeetingModel> all = meetingDAO.getAll();
        List<MeetingModel> usersWithoutPairs = new ArrayList<>();

        for (MeetingModel meetingModel : all) {
            if (meetingModel.getMeetingStateId() == MeetingState.W_NOT_HAVE_PARTNER.id()) {
                usersWithoutPairs.add(meetingModel);
            }
        }

        /* сначала пользователи, которые не получили пару на предыдущей неделе */

        big: for (int i = 0; i < usersWithoutPairs.size(); i++) {
            MeetingModel usersWithoutPair = usersWithoutPairs.get(i);

            /* если уже найдена пара за этот цикл */
            if (usersWithoutPair.getMeetingStateId() == MeetingState.M_HAVE_PARTNER.id()) {
                continue;
            }

            for (int j = 0; j < all.size(); j++) {
                MeetingModel userFromAll = all.get(j);

                long withoutPairUserId = usersWithoutPair.getUserId();
                long userId = userFromAll.getUserId();

                /* проверка, что это не один и тот же пользователь */
                if (withoutPairUserId == userId) {
                    continue;
                }

                /* если уже найдена пара за этот цикл */
                if (userFromAll.getMeetingStateId() == MeetingState.M_HAVE_PARTNER.id()) {
                    continue;
                }

                // Если последняя встреча была менее двух месяцев назад, пропустить эту пару
                Optional<FinalMeetingModel> lastMeeting = finalMeetingDAO.getLastMeetingBetweenUsers(withoutPairUserId, userId);
                if (lastMeeting.isPresent()) {
                    LocalDate lastMeetingDate = lastMeeting.get().getDate();
                    LocalDate currentDate = LocalDate.now();

                    long monthsBetween = ChronoUnit.MONTHS.between(lastMeetingDate, currentDate);
                    if (monthsBetween < 2) {
                        continue;
                    }
                }

                /* И проверка, по алгоритму */
                if (algorithm.isIt(userId, withoutPairUserId)) {
                    usersWithoutPair.setUserMeetingId(userId);
                    usersWithoutPair.setMeetingStateId(MeetingState.M_HAVE_PARTNER.id());
                    meetingDAO.update(usersWithoutPair);

                    userFromAll.setUserMeetingId(withoutPairUserId);
                    userFromAll.setMeetingStateId(MeetingState.M_HAVE_PARTNER.id());
                    meetingDAO.update(userFromAll);

                    UserModel userModel = userDAO.get(userId).get();
                    ChatWork.sendMessage(withoutPairUserId, getMessageWithProfile(userModel), ParseMode.MARKDOWNV2, true, true);

                    userModel = userDAO.get(withoutPairUserId).get();
                    ChatWork.sendMessage(userId, getMessageWithProfile(userModel), ParseMode.MARKDOWNV2, true, true);

                    continue big;
                }
            }

            usersWithoutPair.setMeetingStateId(MeetingState.M_NOT_HAVE_PARTNER.id());
            meetingDAO.update(usersWithoutPair);

            ChatWork.sendMessage(usersWithoutPair.getUserId(), ResBundle.getMessage("monday.notFoundPartner"), ParseMode.MARKDOWNV2, true, true);
        }


        /* поиск пары для остальных пользователей */

        List<MeetingModel> others = meetingDAO.getWhere(true, DBConst.T_MEETING_C_MEETING_STATE_ID, MeetingState.S_ACCEPT_MEETING.id());

        big: for (int i = 0; i < others.size(); i++) {
            MeetingModel user = others.get(i);

            /* если уже найдена пара за этот цикл */
            if (user.getMeetingStateId() == MeetingState.M_HAVE_PARTNER.id()) {
                continue;
            }

            for (int j = i+1; j < others.size(); j++) {
                MeetingModel partner = others.get(j);

                long userId = user.getUserId();
                long partnerId = partner.getUserId();

                /* если уже найдена пара за этот цикл */
                if (partner.getMeetingStateId() == MeetingState.M_HAVE_PARTNER.id()) {
                    continue;
                }

                /* проверка, что это не один и тот же пользователь */
                /* И проверка, по алгоритму */
                if (userId != partnerId && algorithm.isIt(partnerId, userId)) {
                    user.setUserMeetingId(partnerId);
                    user.setMeetingStateId(MeetingState.M_HAVE_PARTNER.id());
                    meetingDAO.update(user);

                    partner.setUserMeetingId(userId);
                    partner.setMeetingStateId(MeetingState.M_HAVE_PARTNER.id());
                    meetingDAO.update(partner);

                    UserModel userModel = userDAO.get(partnerId).get();
                    ChatWork.sendMessage(userId, getMessageWithProfile(userModel), ParseMode.MARKDOWNV2, true, true);

                    userModel = userDAO.get(userId).get();
                    ChatWork.sendMessage(partnerId, getMessageWithProfile(userModel), ParseMode.MARKDOWNV2, true, true);

                    continue big;
                }
            }

            user.setMeetingStateId(MeetingState.M_NOT_HAVE_PARTNER.id());
            meetingDAO.update(user);

            ChatWork.sendMessage(user.getUserId(), ResBundle.getMessage("monday.notFoundPartner"), ParseMode.MARKDOWNV2, true, true);
        }
    }

    public void execute() {
        skipWhoNotHavePartner();
        skipWhoOther();

        getPairs();
    }
}

package ru.meetingbot.deamon.day;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.DBConst;
import ru.meetingbot.db.FinalMeetingState;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.db.model.MeetingModel;
import ru.meetingbot.db.model.UserModel;
import ru.meetingbot.deamon.algorithm.Algorithm;
import ru.meetingbot.util.StringMarkdownV2;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class WednesdayPartTwo {

    private String getProfile(UserModel userModel) {
        String userName = StringMarkdownV2.getString(userModel.getUserName());
        String fullName = StringMarkdownV2.getString(userModel.getFullName());
        String profileLink = StringMarkdownV2.getString(userModel.getProfileLink());
        String instagram = "[" + profileLink + "]" + "(https://www.instagram.com/" + profileLink + "/)";
        String job = StringMarkdownV2.getString(userModel.getJob());
        String hobbie = StringMarkdownV2.getString(userModel.getHobbie());
        Short age = userModel.getYearsOfExperience();

        String reachOut = "__[Telegram]" + "(https://t.me/" + userName + ")__";

        String separator = ResBundle.getMessage("writeYearsOfExperienceState.congratulation.separator");
        String text = new StringBuilder()
                .append(ResBundle.getMessage("monday.beforeUsername")).append(userName).append(ResBundle.getMessage("monday.afterUsername"))
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.name")).append(separator).append(fullName).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.profileLink")).append(separator).append(instagram).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.job")).append(separator).append(job).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.hobbie")).append(separator).append(hobbie).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.age")).append(separator).append(age).append("\n")
                .append(ResBundle.getMessage("monday.beforeReachOut")).append(reachOut).append(ResBundle.getMessage("monday.afterReachOut"))

                .toString();

        return text;
    }

    private void whoDidNotAnswer() {
        MeetingDAO meetingDAO = new MeetingDAO();
        List<MeetingModel> list = meetingDAO.getWhere(true, DBConst.T_MEETING_C_MEETING_STATE_ID, MeetingState.M_HAVE_PARTNER.id());

        for (MeetingModel meetingModel : list) {
            meetingModel.setMeetingStateId(MeetingState.W_DID_NOT_ANSWER.id());
            meetingDAO.update(meetingModel);
        }
    }

    private void whoNotHavePair() {
        MeetingDAO meetingDAO = new MeetingDAO();
        List<MeetingModel> list = meetingDAO.getWhere(true, DBConst.T_MEETING_C_MEETING_STATE_ID, MeetingState.W_NOT_HAVE_PARTNER.id());

        //* ответил ли их партнёр, что нет пары? */
        for (MeetingModel meetingModel : list) {
            Long userMeetingId = meetingModel.getUserMeetingId();
            if (userMeetingId == null) {
                continue;
            }

            Optional<MeetingModel> model = meetingDAO.get(userMeetingId);

            MeetingModel partnerMeetingModel = model.get();

            if (partnerMeetingModel.getMeetingStateId() != MeetingState.W_NOT_HAVE_PARTNER.id()) {
                partnerMeetingModel.setMeetingStateId(MeetingState.W_NOT_HAVE_PARTNER.id());
                meetingDAO.update(partnerMeetingModel);

                ChatWork.sendMessage(partnerMeetingModel.getUserId(), ResBundle.getMessage("wednesdayPartTwo.whoNotHavePair"), ParseMode.MARKDOWNV2);
            }
        }
    }

    private void whoAllWithoutPair() {
        Algorithm algorithm = new Algorithm();
        UserDAO userDAO = new UserDAO();
        MeetingDAO meetingDAO = new MeetingDAO();
        FinalMeetingDAO finalMeetingDAO = new FinalMeetingDAO();

        List<MeetingModel> list = meetingDAO.getWhere(false, DBConst.T_MEETING_C_MEETING_STATE_ID, MeetingState.W_NOT_HAVE_PARTNER.id(),
                DBConst.T_MEETING_C_MEETING_STATE_ID, MeetingState.M_NOT_HAVE_PARTNER.id());

        big: for (int i = 0; i < list.size(); i++) {
            MeetingModel user = list.get(i);

            // добавление в лист партнёра, у которого состояние "есть пара в понедельник"
            if (user.getUserMeetingId() != null) {
                Long userMeetingId = user.getUserMeetingId();
                MeetingModel meetingModel = meetingDAO.get(userMeetingId).get();
                if (meetingModel.getMeetingStateId() == MeetingState.M_HAVE_PARTNER.id()) {
                    meetingModel.setMeetingStateId(MeetingState.W_NOT_HAVE_PARTNER.id());
//                    meetingModel.setUserMeetingId(null);
                    list.add(meetingModel);
                }
            }

            /* если уже найдена пара за этот цикл */
            if (user.getMeetingStateId() == MeetingState.W_HAVE_PARTNER.id()) {
                continue;
            }

            for (int j = i+1; j < list.size(); j++) {
                MeetingModel partner = list.get(j);

                long userId = user.getUserId();
                long partnerId = partner.getUserId();

                /* проверка, что это не один и тот же пользователь */
                if (userId == partnerId) {
                    continue;
                }

                /* если уже найдена пара за этот цикл */
                if (partner.getMeetingStateId() == MeetingState.W_HAVE_PARTNER.id()) {
                    continue;
                }

                /* если это тот же партнёр */
                if (user.getUserMeetingId() != null && user.getUserMeetingId() == partnerId) {
                    continue;
                }

                // Если последняя встреча была менее двух месяцев назад, пропустить эту пару
                Optional<FinalMeetingModel> lastMeeting = finalMeetingDAO.getLastMeetingBetweenUsers(partnerId, userId);
                if (lastMeeting.isPresent()) {
                    LocalDate lastMeetingDate = lastMeeting.get().getDate();
                    LocalDate currentDate = LocalDate.now();

                    long monthsBetween = ChronoUnit.MONTHS.between(lastMeetingDate, currentDate);
                    if (monthsBetween < 2) {
                        continue;
                    }
                }

                /* И проверка, по алгоритму */
                if (algorithm.isIt(partnerId, userId)) {

                    //удалить из партнёров, тех кому ищатся новые партнёры
                    List<MeetingModel> where = meetingDAO.getWhere(true, DBConst.T_MEETING_C_USER_MEETING_ID, userId);
                    if (!where.isEmpty()) {
                        MeetingModel meetingModel = where.get(0);
                        meetingModel.setUserMeetingId(null);
                        meetingDAO.update(meetingModel);
                    }
                    where = meetingDAO.getWhere(true, DBConst.T_MEETING_C_USER_MEETING_ID, partnerId);
                    if (!where.isEmpty()) {
                        MeetingModel meetingModel = where.get(0);
                        meetingModel.setUserMeetingId(null);
                        meetingDAO.update(meetingModel);
                    }


                    user.setUserMeetingId(partnerId);
                    user.setMeetingStateId(MeetingState.W_HAVE_PARTNER.id());
                    meetingDAO.update(user);

                    partner.setUserMeetingId(userId);
                    partner.setMeetingStateId(MeetingState.W_HAVE_PARTNER.id());
                    meetingDAO.update(partner);

                    UserModel userModel = userDAO.get(partnerId).get();
                    String userName = StringMarkdownV2.getString(userModel.getUserName());
                    ChatWork.sendMessage(userId, getProfile(userModel), ParseMode.MARKDOWNV2, true, true);

                    userModel = userDAO.get(userId).get();
                    userName = StringMarkdownV2.getString(userModel.getUserName());
                    ChatWork.sendMessage(partnerId, getProfile(userModel), ParseMode.MARKDOWNV2, true, true);

                    continue big;
                }
            }

            user.setMeetingStateId(MeetingState.W_NOT_HAVE_PARTNER.id());
            user.setUserMeetingId(null);
            meetingDAO.update(user);

            finalMeetingDAO.create(new FinalMeetingModel(user.getUserId(), null, FinalMeetingState.W_NOT_HAVE_PARTNER.id(), null, LocalDate.now()));

            ChatWork.sendMessage(user.getUserId(), ResBundle.getMessage("monday.notFoundPartner"), ParseMode.MARKDOWNV2, true, true);
        }
    }

    public void execute() {
        whoDidNotAnswer();
        whoNotHavePair();

        whoAllWithoutPair();
    }
}

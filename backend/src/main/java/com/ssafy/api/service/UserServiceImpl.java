package com.ssafy.api.service;

import com.ssafy.api.request.UserUpdatePutReq;
import com.ssafy.db.entity.auth.*;
import com.ssafy.db.entity.board.Board;
import com.ssafy.db.repository.auth.*;
import com.ssafy.db.repository.board.BoardImageRepository;
import com.ssafy.db.repository.board.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;


    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    CounselingHistoryRepository counselingHistoryRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardImageRepository boardImageRepository;

    @Autowired
    S3Uploader s3Uploader;


    @Override
    public User createUser(String access_Token, String refresh_Token, HashMap<String, Object> userInfo) {

        User user = new User();
        UserProfile userProfile = new UserProfile();

        String id = (String) userInfo.get("userid");
        String name = (String) userInfo.get("name");
        String profileImageUrl = (String)userInfo.get("profileImageUrl");
        String email = (String) userInfo.get("email");
        String accessToken = (String) userInfo.get("accessToken");
        String refreshToken = (String) userInfo.get("refreshToken");
        String phoneNumber = (String) userInfo.get("phoneNumber");

        System.out.println(id+" "+name+profileImageUrl+email+accessToken+refreshToken+phoneNumber);

        /* User ?????? */
        user.setId(id);
        User returnUser = userRepository.save(user);

        /* User Profile ?????? */
        userProfile.setUserId(returnUser);
        userProfile.setName(name);
        userProfile.setProfileImageUrl(profileImageUrl);
        userProfile.setEmail(email);
        userProfile.setPhoneNumber(phoneNumber);

        userProfileRepository.save(userProfile);

        /* User Token ?????? */
        UserToken userToken = new UserToken();
        userToken.setUserId(user);
        userToken.setAccessToken(accessToken);
        userToken.setRefreshToken(refreshToken);

        userTokenRepository.save(userToken);

        return returnUser;
    }

    @Override
    public User getUserById(String id) {
        // ????????? ?????? ?????? ?????? (userId ??? ?????? ??????).
        Optional<User> user = userRepository.findUserById(id);

        if(user.isPresent()) return user.get();
        return null;

    }

    @Override
    public UserProfile updateUserProfile(String id, UserUpdatePutReq userUpdatePutReq) throws IOException {
        User user = userRepository.findUserById(id).get();

        System.out.println(user + " " + userUpdatePutReq.getBirth() + " " + userUpdatePutReq.getEmail() + " " +  userUpdatePutReq.getPhoneNumber() + " " + userUpdatePutReq.getName());
        UserProfile userProfile = userProfileRepository.findByUserId(user).get();

        if(userUpdatePutReq.getFile()!=null){
            String profileImageUrl = s3Uploader.upload(userUpdatePutReq.getFile(),"static");
            userProfile.setProfileImageUrl("https://"+S3Uploader.CLOUD_FRONT_DOMAIN_NAME+"/"+profileImageUrl);
        }
        userProfile.setName(userUpdatePutReq.getName());
        userProfile.setEmail(userUpdatePutReq.getEmail());
        userProfile.setPhoneNumber(userUpdatePutReq.getPhoneNumber());
        userProfile.setBirth(userUpdatePutReq.getBirth());
        userProfileRepository.save(userProfile);
        return userProfile;
    }

    @Override
    public UserProfile getUserProfile(String id) {
        Optional<User> user = userRepository.findUserById(id);
        if(user.isPresent()) {
            Optional<UserProfile> userProfile = userProfileRepository.findByUserId(user.get());
            if(userProfile.isPresent()){
                return userProfile.get();
            }
        }
        return null;

    }

    @Override
    public boolean deleteUser(String id) {
        if(getUserById(id)!=null){

            User user = userRepository.findById(id).get();
            Optional<UserProfile> userProfile = userProfileRepository.findByUserId(user);

            if(userProfile.isPresent()) {
                Optional<UserToken> userToken = userTokenRepository.findByUserId(user);
                Optional<List<Bookmark>> bookmarkList = bookmarkRepository.findBookmarksByUserId(userProfile.get());
                Optional<List<Board>> boardList = boardRepository.findBoardsByUserId(id);
                Optional<List<CounselingHistory>> counselingHistoryList = counselingHistoryRepository.findCounselingHistoriesByWriter(id);
                Optional<List<CounselingHistory>> applicantList = counselingHistoryRepository.findCounselingHistoriesByApplicantId(userProfile.get());
                if(bookmarkList.isPresent()){
                    bookmarkRepository.deleteBookmarksByUserId(userProfile.get());
                }
                if(boardList.isPresent()){
                    for (Board board:boardList.get()) {
                        boardImageRepository.deleteBoardImagesByBoardId(board);
                    }
                    boardRepository.deleteBoardsByUserId(id);
                }
                if(counselingHistoryList.isPresent()){
                    counselingHistoryRepository.deleteCounselingHistoriesByWriter(id);
                }
                if(applicantList.isPresent()){
                    counselingHistoryRepository.deleteCounselingHistoriesByApplicantId(userProfile.get());
                }
                if(userToken.isPresent()){
                    userTokenRepository.delete(userToken.get());
                }
                userProfileRepository.delete(userProfile.get());
            }
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    @Override
    public String getUserName(String id) {
        User user = getUserById(id);
        if (user == null) return null;

        Optional<UserProfile> userProfile = userProfileRepository.findByUserId(user);
        if (userProfile.isPresent()) {
            return userProfile.get().getName();

        }

        return null;
    }

    @Override
    public List<Bookmark> getBookmarkList(String id) {
        Optional<User> user = userRepository.findUserById(id);
        if(user.isPresent()) {
            Optional<UserProfile> userProfile = userProfileRepository.findByUserId(user.get());
            if (userProfile.isPresent()) {
                Optional<List<Bookmark>> bookmarkList = bookmarkRepository.findBookmarksByUserId(userProfile.get());
                if (bookmarkList.isPresent()) {
                    return bookmarkList.get();
                }
            }

        }
        return null;
    }

    @Override
    public List<CounselingHistory> getCounselingResult(String id) {
        Optional<User> user = userRepository.findUserById(id);
        List<CounselingHistory> resultList = new ArrayList<>();
        if(user.isPresent()){
            Optional<UserProfile> userProfile = userProfileRepository.findByUserId(user.get());
            if(userProfile.isPresent()){
                List<CounselingHistory> List = counselingHistoryRepository.findCounselingHistoriesByApplicantId(userProfile.get()).get();
                for (CounselingHistory counselingHistory: List) {
                    if(counselingHistory.getBoardType().equals("??????") || counselingHistory.getBoardType().equals("??????")){
                        resultList.add(counselingHistory);
                    }
                }
            }

        }
        if(resultList!=null){
            return resultList;
        }
        return null;
    }

    @Override
    public List<CounselingHistory> getApplicantList(String id) {
        Optional<List<CounselingHistory>> applicantList = counselingHistoryRepository.findCounselingHistoriesByWriter(id);
        if(applicantList.isPresent()){
            return applicantList.get();
        }
        return null;
    }

    @Override
    public CounselingHistory getCounselingById(Long counselingId) {
        Optional<CounselingHistory> counselingHistory = counselingHistoryRepository.findById(counselingId);
        if(counselingHistory.isPresent())
            return counselingHistory.get();
        return null;
    }

    @Override
    public void deleteUserProfileImageByUrl(String delfile) {
        if(delfile!=null){
            s3Uploader.delete(delfile);
        }
    }

}

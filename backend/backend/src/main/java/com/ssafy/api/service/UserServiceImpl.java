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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepositorySupport userRepositorySupport;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserProfileRepositorySupport userProfileRepositorySupport;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    UserTokenRepositorySupport userTokenRepositorySupport;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    BookmarkRepositorySupport bookmarkRepositorySupport;

    @Autowired
    CounselingHistoryRepository counselingHistoryRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardImageRepository boardImageRepository;


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


    @Value("${profileImg.path}")
    private String uploadFolder;

//    @Override
//    public UserProfile updateUserProfile(String id, UserUpdatePutReq userUpdatePutReq, MultipartFile multipartFile) {
//        User user = userRepositorySupport.findUserById(id).get();
//        Optional<UserProfile> userProfile = userProfileRepositorySupport.findUserByUserId(user);
//
//        String imageFileName = user.getId() + "_" + multipartFile.getOriginalFilename();
//        Path imageFilePath = Paths.get(uploadFolder + imageFileName);
//
//        // ????????? ????????? ???????????? ??????
//        if(multipartFile.getSize() != 0){
//            try{
//                // ?????? ????????? ????????? ?????? ??????
//                if(userProfile.get().getProfileImageUrl()!=null){
//                    File file = new File(uploadFolder + userProfile.get().getProfileImageUrl());
//                    file.delete();
//                }
//                Files.write(imageFilePath, multipartFile.getBytes());
//                System.out.println("File Path: " + imageFilePath);
//            } catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//
//        userProfile.get().setName(userUpdatePutReq.getName());
//        userProfile.get().setProfileImageUrl(imageFileName);
//        userProfileRepository.save(userProfile.get());
//
//        return userProfile.get();
//    }

    @Override
    public UserProfile updateUserProfile(String id, UserUpdatePutReq userUpdatePutReq) {
        User user = userRepository.findById(id).get();
        System.out.println(user + " " + userUpdatePutReq.getBirth() + " " + userUpdatePutReq.getEmail() + " " +  userUpdatePutReq.getPhoneNumber() + " " + userUpdatePutReq.getName());

        Optional<UserProfile> userProfile = userProfileRepositorySupport.findUserByUserId(user);
        userProfile.get().setName(userUpdatePutReq.getName());
        userProfile.get().setEmail(userUpdatePutReq.getEmail());
        userProfile.get().setPhoneNumber(userUpdatePutReq.getPhoneNumber());
        userProfile.get().setBirth(userUpdatePutReq.getBirth());
        userProfileRepository.save(userProfile.get());
        return userProfile.get();
    }

    @Override
    public UserProfile getUserProfile(String id) {
        Optional<User> user = userRepository.findById(id);
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
            User user = userRepositorySupport.findUserById(id).get();
            Optional<UserProfile> userProfile = userProfileRepositorySupport.findUserByUserId(user);
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

        if(user.isPresent()){
            Optional<UserProfile> userProfile = userProfileRepository.findByUserId(user.get());
            if(userProfile.isPresent()){
                Optional<List<CounselingHistory>> resultList = counselingHistoryRepository.findCounselingHistoriesByApplicantId(userProfile.get());
                if(resultList.isPresent()){
                    return resultList.get();
                }
            }
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

}

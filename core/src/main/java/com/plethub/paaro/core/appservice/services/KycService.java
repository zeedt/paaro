package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.KycRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.KycResponseModel;
import com.plethub.paaro.core.appservice.dao.IdentificationNumberVerification;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.KycStatus;
import com.plethub.paaro.core.appservice.repository.WalletRepository;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import com.plethub.paaro.core.models.Kyc;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.models.Wallet;
import com.plethub.paaro.core.appservice.repository.KycRepository;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.impl.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class KycService {
    @Autowired
    private WalletService walletService;

    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    @Value("${paaro.file.folder:/Users/zeed/Documents/uploads}")
    private String basePath;

    @Autowired
    private KycRepository kycRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private KycEmailService kycEmailService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IdentificationNumberVerification identificationNumberVerification;

    private Logger logger = LoggerFactory.getLogger(KycService.class.getName());

    @Autowired
    private WalletRepository walletRepository;

    public KycResponseModel uploadBvnOrIdentificationNumber(KycRequestModel kycRequestModel) {

        if (kycRequestModel == null || StringUtils.isEmpty(kycRequestModel.getBvnOrIdentificationNumber())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Bvn Or Identification number cannot be blank");
        }

        if (StringUtils.isEmpty(kycRequestModel.getCurrencyType())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(kycRequestModel.getCurrencyType().trim());

        if (wallet == null) {
            return KycResponseModel.fromNarration(ApiResponseCode.UNABLE_TO_PROCESS, String.format("Wallet of currency type %s not found for user", kycRequestModel.getCurrencyType()));
        }

        Boolean isBvnValid = (kycRequestModel.getCurrencyType().trim().equalsIgnoreCase(nairaCurrency)) ?
                identificationNumberVerification.verifyBvnForNairaAccount(wallet.getManagedUser()) : identificationNumberVerification.verifyIdentificationNumberForOtherCurrency(wallet.getManagedUser());

        if (!isBvnValid) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid Bvn");
        }

        Kyc kyc = findOrCreateWalletForWallet(wallet);
        kyc.setBvn(kycRequestModel.getBvnOrIdentificationNumber().trim());
        kyc.setBvnUploadedDate(new Date());
        kyc.setBvnKycStatus(KycStatus.PENDING_VERIFICATION);
        kycRepository.save(kyc);

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Bvn uploaded successfully");

    }

    public KycResponseModel uploadUtilityBill(KycRequestModel kycRequestModel) throws IOException {

        if (kycRequestModel == null || StringUtils.isEmpty(kycRequestModel.getBase64file())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Utility bill base 64 image cannot be blank");
        }

        if (StringUtils.isEmpty(kycRequestModel.getCurrencyType())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(kycRequestModel.getCurrencyType().trim());

        if (wallet == null) {
            return KycResponseModel.fromNarration(ApiResponseCode.UNABLE_TO_PROCESS, String.format("Wallet of currency type %s not found for user", kycRequestModel.getCurrencyType()));
        }

        File file = new File(String.format("%s/%s/%s/utility-bill.png",basePath,wallet.getManagedUser().getDisplayName(),wallet.getCurrency().getType()));

        file.getParentFile().mkdirs();
        file.createNewFile();

        BufferedImage image = null;
        byte[] imageByte;

        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(kycRequestModel.getBase64file());
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

        // write the image to a file
        ImageIO.write(image, "png", file);

        Kyc kyc = findOrCreateWalletForWallet(wallet);
        kyc.setUtilityBillFileName(file.getAbsolutePath());
        kyc.setUtilityBillUploadedDate(new Date());
        kyc.setUtilityBillKycStatus(KycStatus.PENDING_VERIFICATION);
        kycRepository.save(kyc);

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Utility uploaded successfully");

    }
    public KycResponseModel uploadValidId(KycRequestModel kycRequestModel) throws IOException {

        if (kycRequestModel == null || StringUtils.isEmpty(kycRequestModel.getBase64file())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Valid Id base 64 image cannot be blank");
        }

        if (StringUtils.isEmpty(kycRequestModel.getCurrencyType())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(kycRequestModel.getCurrencyType().trim());

        if (wallet == null) {
            return KycResponseModel.fromNarration(ApiResponseCode.UNABLE_TO_PROCESS, String.format("Wallet of currency type %s not found for user", kycRequestModel.getCurrencyType()));
        }

        File file = new File(String.format("%s/%s/%s/valid-id.png",basePath,wallet.getManagedUser().getDisplayName(), wallet.getCurrency().getType()));

        file.getParentFile().mkdirs();
        file.createNewFile();

        BufferedImage image = null;
        byte[] imageByte;

        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(kycRequestModel.getBase64file().replace("data:image/png;base64,",""));
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

        ImageIO.write(image, "png", file);

        Kyc kyc = findOrCreateWalletForWallet(wallet);
        kyc.setValidIdFileName(file.getAbsolutePath());
        kyc.setValidIdUploadedDate(new Date());
        kyc.setValidIdKycStatus(KycStatus.PENDING_VERIFICATION);
        kycRepository.save(kyc);

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Valid Id uploaded successfully");

    }

    public KycResponseModel uploadPassportPhoto(KycRequestModel kycRequestModel) throws IOException {

        if (kycRequestModel == null || StringUtils.isEmpty(kycRequestModel.getBase64file())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Valid Id base 64 image cannot be blank");
        }

        if (StringUtils.isEmpty(kycRequestModel.getCurrencyType())) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(kycRequestModel.getCurrencyType().trim());

        if (wallet == null) {
            return KycResponseModel.fromNarration(ApiResponseCode.UNABLE_TO_PROCESS, String.format("Wallet of currency type %s not found for user", kycRequestModel.getCurrencyType()));
        }

        File file = new File(String.format("%s/%s/%s/passport-photo.png",basePath,wallet.getManagedUser().getDisplayName(), wallet.getCurrency().getType()));

        file.getParentFile().mkdirs();
        file.createNewFile();

        BufferedImage image = null;
        byte[] imageByte;

        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(kycRequestModel.getBase64file().replace("data:image/png;base64,",""));
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

        ImageIO.write(image, "png", file);

        Kyc kyc = findOrCreateWalletForWallet(wallet);
        kyc.setPassportPhotoFileName(file.getAbsolutePath());
        kyc.setPassportPhotoUploadedDate(new Date());
        kyc.setPassportPhotoKycStatus(KycStatus.PENDING_VERIFICATION);
        kycRepository.save(kyc);

        savePassportForOtherWallets(wallet, file.getAbsolutePath());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Passport photo uploaded successfully");

    }

    private void savePassportForOtherWallets(Wallet wallet, String path) {
        if (wallet == null)
            return;
        ManagedUser managedUser = wallet.getManagedUser();

        List<Wallet> wallets = walletRepository.findAllByManagedUser_Id(managedUser.getId());

        wallets.stream().parallel().forEach(walletsItem -> {
            try {
                if (walletsItem != null && walletsItem.getId()!= null && wallet.getId() != null && walletsItem.getId() != wallet.getId()) {
                    Kyc kyc = findOrCreateWalletForWallet(walletsItem);
                    kyc.setPassportPhotoFileName(path);
                    kyc.setPassportPhotoUploadedDate(new Date());
                    kyc.setPassportPhotoKycStatus(KycStatus.PENDING_VERIFICATION);
                    kycRepository.save(kyc);
                }
            } catch (Exception e) {
                logger.error("System error occurred while saving passport for wallet due to ", e);
            }
        });
    }

    public KycResponseModel getUserKycByCurrency(String currencyType) throws IOException {

        if (StringUtils.isEmpty(currencyType)) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(currencyType);

        if (wallet == null) {
            return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
        }

        Kyc kyc = kycRepository.findByWallet_Id(wallet.getId());
        if (kyc == null) {
            KycResponseModel kycResponseModel = KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No kyc provided by this customer");
            kycResponseModel.setBvnKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setValidIdKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setUtilityBillKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setPassportPhotoKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            return kycResponseModel;
        }

        String validIdData = getBase64StringFromFilePath(kyc.getValidIdFileName());
        String utilityBillData = getBase64StringFromFilePath(kyc.getUtilityBillFileName());
        String passportPhotoData = getBase64StringFromFilePath(kyc.getPassportPhotoFileName());


        KycResponseModel kycResponseModel = new KycResponseModel();
        kycResponseModel.setId(kyc.getId());
        kycResponseModel.setUtilityBillData(utilityBillData);
        kycResponseModel.setValidIdData(validIdData);
        kycResponseModel.setPassportPhotoData(passportPhotoData);
        kycResponseModel.setBvnOrIdentificationNo(kyc.getBvn());
        kycResponseModel.setBvnOrIdentificationNumberUploaded((StringUtils.isEmpty(kyc.getBvn()) || kyc.getBvnUploadedDate() == null) ? false : true);
        kycResponseModel.setUtilityBillUploaded((StringUtils.isEmpty(kyc.getUtilityBillFileName()) || kyc.getUtilityBillUploadedDate() == null) ? false : true);
        kycResponseModel.setValidIdUploaded((StringUtils.isEmpty(kyc.getValidIdFileName()) || kyc.getValidIdKycStatus() == null) ? false : true);
        kycResponseModel.setPassportPhotoUploaded((StringUtils.isEmpty(kyc.getPassportPhotoFileName()) || kyc.getPassportPhotoKycStatus() == null) ? false : true);
        kycResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        kycResponseModel.setBvnKycStatus(kyc.getBvnKycStatus());
        kycResponseModel.setUtilityBillKycStatus(kyc.getUtilityBillKycStatus());
        kycResponseModel.setValidIdKycStatus(kyc.getValidIdKycStatus());
        kycResponseModel.setPassportPhotoKycStatus(kyc.getPassportPhotoKycStatus());


        return kycResponseModel;
    }

    public KycResponseModel getKycByCurrencyAndUser(String currencyType, ManagedUser managedUser) throws IOException {

        if (StringUtils.isEmpty(currencyType)) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Wallet wallet = walletService.getUserWalletByCurrencyType(managedUser, currencyType);

        if (wallet == null) {
            return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
        }

        Kyc kyc = kycRepository.findByWallet_Id(wallet.getId());
        if (kyc == null) {
            KycResponseModel kycResponseModel = KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No kyc provided by this customer");
            kycResponseModel.setBvnKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setValidIdKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setUtilityBillKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setPassportPhotoKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            return kycResponseModel;
        }

        String validIdData = getBase64StringFromFilePath(kyc.getValidIdFileName());
        String utilityBillData = getBase64StringFromFilePath(kyc.getUtilityBillFileName());
        String passportPhotoData = getBase64StringFromFilePath(kyc.getPassportPhotoFileName());


        KycResponseModel kycResponseModel = new KycResponseModel();
        kycResponseModel.setId(kyc.getId());
        kycResponseModel.setUtilityBillData(utilityBillData);
        kycResponseModel.setValidIdData(validIdData);
        kycResponseModel.setPassportPhotoData(passportPhotoData);
        kycResponseModel.setBvnOrIdentificationNo(kyc.getBvn());
        kycResponseModel.setBvnOrIdentificationNumberUploaded((StringUtils.isEmpty(kyc.getBvn()) || kyc.getBvnUploadedDate() == null) ? false : true);
        kycResponseModel.setUtilityBillUploaded((StringUtils.isEmpty(kyc.getUtilityBillFileName()) || kyc.getUtilityBillUploadedDate() == null) ? false : true);
        kycResponseModel.setValidIdUploaded((StringUtils.isEmpty(kyc.getValidIdFileName()) || kyc.getValidIdKycStatus() == null) ? false : true);
        kycResponseModel.setPassportPhotoUploaded((StringUtils.isEmpty(kyc.getPassportPhotoFileName()) || kyc.getPassportPhotoKycStatus() == null) ? false : true);
        kycResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        kycResponseModel.setBvnKycStatus(kyc.getBvnKycStatus());
        kycResponseModel.setUtilityBillKycStatus(kyc.getUtilityBillKycStatus());
        kycResponseModel.setValidIdKycStatus(kyc.getValidIdKycStatus());
        kycResponseModel.setPassportPhotoKycStatus(kyc.getPassportPhotoKycStatus());


        return kycResponseModel;
    }

    public KycResponseModel getUserKycFlagByCurrency(String currencyType) throws IOException {
        KycResponseModel kycResponseModel = new KycResponseModel();

        if (StringUtils.isEmpty(currencyType)) {
            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Wallet wallet = walletService.getCurrentLoggedInUserWalletByCurrencyType(currencyType);

        if (wallet == null) {
            return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
        }

        Kyc kyc = kycRepository.findByWallet_Id(wallet.getId());
        if (kyc == null) {
            kycResponseModel.setValidIdUploaded(false);
            kycResponseModel.setUtilityBillUploaded(false);
            kycResponseModel.setBvnOrIdentificationNumberUploaded(false);
            kycResponseModel.setPassportPhotoUploaded(false);
            kycResponseModel.setBvnKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setUtilityBillKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setValidIdKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setPassportPhotoKycStatus(KycStatus.PENDING_UPLOAD);
            kycResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            return kycResponseModel;
        }

        kycResponseModel.setId(kyc.getId());
        kycResponseModel.setBvnOrIdentificationNumberUploaded((StringUtils.isEmpty(kyc.getBvn()) || kyc.getBvnUploadedDate() == null) ? false : true);
        kycResponseModel.setUtilityBillUploaded((StringUtils.isEmpty(kyc.getUtilityBillFileName()) || kyc.getUtilityBillUploadedDate() == null) ? false : true);
        kycResponseModel.setValidIdUploaded((StringUtils.isEmpty(kyc.getValidIdFileName()) || kyc.getUtilityBillUploadedDate() == null) ? false : true);
        kycResponseModel.setPassportPhotoUploaded((StringUtils.isEmpty(kyc.getPassportPhotoFileName()) || kyc.getPassportPhotoUploadedDate() == null) ? false : true);
        kycResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        kycResponseModel.setBvnKycStatus(kyc.getBvnKycStatus());
        kycResponseModel.setUtilityBillKycStatus(kyc.getUtilityBillKycStatus());
        kycResponseModel.setValidIdKycStatus(kyc.getValidIdKycStatus());
        kycResponseModel.setPassportPhotoKycStatus(kyc.getPassportPhotoKycStatus());

        return kycResponseModel;
    }

//    public KycResponseModel getUserKycDetails(Long id) throws IOException {
//        List<KycResponseModel> kycResponseModelList = new ArrayList<>();
//        KycResponseModel kycResponseModel = new KycResponseModel();
//
////        if (StringUtils.isEmpty(id)) {
////            return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
////        }
//
//        List<Wallet> wallet = walletService.findWalletsByUserId(id).getWalletList();
//
//        if (wallet == null) {
//            return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Wallet not found for user");
//        }
//
//
//
//        Kyc kyc = kycRepository.findByWallet_Id(wallet.getId());
//        if (kyc == null) {
//            kycResponseModel.setValidIdUploaded(false);
//            kycResponseModel.setUtilityBillUploaded(false);
//            kycResponseModel.setBvnOrIdentificationNumberUploaded(false);
//            kycResponseModel.setBvnKycStatus(KycStatus.PENDING_UPLOAD);
//            kycResponseModel.setUtilityBillKycStatus(KycStatus.PENDING_UPLOAD);
//            kycResponseModel.setValidIdKycStatus(KycStatus.PENDING_UPLOAD);
//            return kycResponseModel;
//        }
//
//        kycResponseModel.setBvnOrIdentificationNumberUploaded((StringUtils.isEmpty(kyc.getBvn()) || kyc.getBvnUploadedDate() == null) ? false : true);
//        kycResponseModel.setUtilityBillUploaded((StringUtils.isEmpty(kyc.getUtilityBillFileName()) || kyc.getUtilityBillUploadedDate() == null) ? false : true);
//        kycResponseModel.setValidIdUploaded((StringUtils.isEmpty(kyc.getValidIdFileName()) || kyc.getUtilityBillUploadedDate() == null) ? false : true);
//        kycResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
//        kycResponseModel.setBvnKycStatus(kyc.getBvnKycStatus());
//        kycResponseModel.setUtilityBillKycStatus(kyc.getUtilityBillKycStatus());
//        kycResponseModel.setValidIdKycStatus(kyc.getValidIdKycStatus());
//
//        return kycResponseModel;
//    }

    private String getBase64StringFromFilePath(String fileName) throws IOException {

        if (StringUtils.isEmpty(fileName)) {
            return null;
        }

        byte[] fileToByteArray = FileUtils.readFileToByteArray(new File(fileName));

        BASE64Encoder base64Encoder = new BASE64Encoder();
        String base64Data = base64Encoder.encode(fileToByteArray);

        return base64Data;
    }

    public Kyc findOrCreateWalletForWallet(Wallet wallet) {
        Kyc kyc = kycRepository.findByWallet_Id(wallet.getId());
        if (kyc == null) {
            kyc = new Kyc();
            kyc.setWallet(wallet);
            if (wallet.getCurrency() != null && !StringUtils.isEmpty(wallet.getCurrency().getType())) {
                if (nairaCurrency.trim().equalsIgnoreCase(wallet.getCurrency().getType().trim())) {
                    kyc.setBvnKycStatus(KycStatus.PENDING_UPLOAD);
                } else {
                    kyc.setBvnKycStatus(KycStatus.VERIFIED);
                }
            } else {
                kyc.setBvnKycStatus(KycStatus.PENDING_UPLOAD);
            }
            kyc.setUtilityBillKycStatus(KycStatus.PENDING_UPLOAD);
            kyc.setValidIdKycStatus(KycStatus.PENDING_UPLOAD);
            kyc.setPassportPhotoKycStatus(KycStatus.PENDING_UPLOAD);
            kyc = kycRepository.save(kyc);
        }
        return kyc;
    }

    public KycResponseModel verifyBvnKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "BVN Kyc not found");
        if (kyc.getBvnKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setBvnKycStatus(KycStatus.VERIFIED);
        kycRepository.save(kyc);
        kycEmailService.sendApprovedKycEmail(user.getEmail(), "BVN", user.getFirstName());
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User verified Bvn kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "BVN Kyc successfully verified");

    }

    public KycResponseModel verifyValidIdKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Valid Id Kyc not found");
        if (kyc.getValidIdKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setValidIdKycStatus(KycStatus.VERIFIED);
        kycRepository.save(kyc);
        kycEmailService.sendApprovedKycEmail(user.getEmail(), "VALID ID", user.getFirstName());
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User verified valid Id kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Valid Id Kyc successfully verified");

    }
    public KycResponseModel verifyUtilityBillKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Utility Kyc not found");
        if (kyc.getUtilityBillKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setUtilityBillKycStatus(KycStatus.VERIFIED);
        kycRepository.save(kyc);
        kycEmailService.sendApprovedKycEmail(user.getEmail(), "UTILITY BILL", user.getFirstName());
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User verified Utility Bill kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Utility Bill Kyc successfully verified");

    }
    public KycResponseModel verifyPassportKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Passport Kyc not found");
        if (kyc.getPassportPhotoKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setPassportPhotoKycStatus(KycStatus.VERIFIED);
        kycRepository.save(kyc);
        kycEmailService.sendApprovedKycEmail(user.getEmail(), "PASSPORT", user.getFirstName());
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User verified Passsport kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Passport Kyc successfully verified");

    }

    public KycResponseModel declineBvnKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Bvn Kyc not found");
        if (kyc.getBvnKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setBvnKycStatus(KycStatus.DECLINED);
        kycRepository.save(kyc);
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User declined Bvn kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Bvn Kyc successfully declined");

    }

    public KycResponseModel declinePassportKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "passport Kyc not found");
        if (kyc.getPassportPhotoKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setPassportPhotoKycStatus(KycStatus.DECLINED);
        kycRepository.save(kyc);
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User declined passport kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Passport Kyc successfully declined");

    }

    public KycResponseModel declineUtilityBillKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Utility Bill Kyc not found");
        if (kyc.getUtilityBillKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setUtilityBillKycStatus(KycStatus.DECLINED);
        kycRepository.save(kyc);
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User declined utility bill kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Utility Bill Kyc successfully declined");

    }

    public KycResponseModel declineValidIdKyc(Long id, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (id == null || id == 0) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null or 0");

        ManagedUser user = userService.getCurrentLoggedInUser();

        if (user == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "User not found");

        Kyc kyc = kycRepository.findOne(id);

        if (kyc == null) return KycResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Valid Id Kyc not found");
        if (kyc.getValidIdKycStatus() != KycStatus.PENDING_VERIFICATION) return KycResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Kyc not awaiting verification");

        kyc.setValidIdKycStatus(KycStatus.DECLINED);
        kycRepository.save(kyc);
        auditLogService.saveAudit(null, objectMapper.writeValueAsString(kyc), Module.KYC, servletRequest,"User declined valid kyc", kyc.getId());

        return KycResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Valid Id Kyc successfully declined");

    }

    public void downloadKycAsAttachment(HttpServletResponse servletResponse, Long kycId, String kycType) throws IOException {
        if (StringUtils.isEmpty(kycType)) return;
        Kyc kyc = kycRepository.findOne(kycId);
        if (kyc == null) return;

        String filePath = "";

        if (kycType.equalsIgnoreCase(GeneralUtil.VALID_ID_KYC)) filePath = kyc.getValidIdFileName();
        if (kycType.equalsIgnoreCase(GeneralUtil.UTILITY_BILL_KYC)) filePath = kyc.getUtilityBillFileName();
        if (kycType.equalsIgnoreCase(GeneralUtil.PASSPORT_PHOTOGRAPH_KYC)) filePath = kyc.getPassportPhotoFileName();

        if (StringUtils.isEmpty(filePath)) return;

        byte[] fileByteArray = FileUtils.readFileToByteArray(new File(filePath));

        BufferedImage bufferedImage = ImageIO.read(new File(filePath));

        String fileName = "Kyc image.png";
        servletResponse.setContentType("image/png");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"" + fileName + "\"";
        servletResponse.setHeader(headerKey, headerValue);

        ImageIO.write(bufferedImage,"png", servletResponse.getOutputStream());

        servletResponse.flushBuffer();

    }

}

#include <jni.h>
#include <string>
#include <android/log.h>

std::string decrypt(const unsigned char* encrypted, size_t size, const std::string& key) {
    std::string result;
    result.reserve(size);

    for (size_t i = 0; i < size; i++) {
        result += static_cast<char>(encrypted[i] ^ key[i % key.length()]);
    }
    return result;
}

const unsigned char ENCRYPTED_API_KEY[] = {9, 64, 30, 0, 61, 6, 103, 58, 32, 122, 20, 33, 38, 14, 11, 122, 47, 19, 53, 85, 59, 71, 10, 7, 89, 54, 60, 121, 55, 90, 35, 127, 46, 84, 12, 40, 0, 93, 12, 69, 49, 49, 92, 36, 60, 6, 36, 86};
const size_t ENCRYPTED_API_KEY_SIZE = 48;

const unsigned char ENCRYPTED_API_URL[] = {7, 71, 27, 67, 23, 81, 29, 64, 14, 67, 4, 69, 9, 89, 26, 71, 23, 26, 26, 82, 29, 86, 74, 8, 93, 2, 64, 69, 94, 68, 31, 90, 14, 86, 1, 24, 64};
const size_t ENCRYPTED_API_URL_SIZE = 37;

const std::string ENCRYPTION_KEY = "o3o3dk2oo3mko6o5dk";

extern "C" JNIEXPORT jstring JNICALL
Java_com_djordjekrutil_fsqhc_core_util_ApiKeyManager_getApiKey(JNIEnv *env, jobject manager) {
    std::string decrypted = decrypt(ENCRYPTED_API_KEY, ENCRYPTED_API_KEY_SIZE, ENCRYPTION_KEY);
    return env->NewStringUTF(decrypted.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_djordjekrutil_fsqhc_core_util_ApiKeyManager_getApiUrl(JNIEnv *env, jobject manager) {
    std::string decrypted = decrypt(ENCRYPTED_API_URL, ENCRYPTED_API_URL_SIZE, ENCRYPTION_KEY);
    return env->NewStringUTF(decrypted.c_str());
}
// composables/useNotificationSettings.js
import { ref, reactive } from 'vue';
import { fetchNotificationSettings } from '../api/setalarm';

export function useNotificationSettings() {
    const notificationSettings = reactive({
        morning: null,
        lunch: null,
        dinner: null,
        sleep: null,
        notificationId: null
    });
    const fetchFailed = ref(false);

    const loadNotificationSettings = async () => {
        try {
            const data = await fetchNotificationSettings();

            notificationSettings.morning = data.morning ?? null;
            notificationSettings.lunch = data.lunch ?? null;
            notificationSettings.dinner = data.dinner ?? null;
            notificationSettings.sleep = data.sleep ?? null;
            notificationSettings.notificationId = data.notificationId ?? null;

            const allNull = [notificationSettings.morning, notificationSettings.lunch, notificationSettings.dinner, notificationSettings.sleep].every(v => v === null);
            fetchFailed.value = allNull;
        } catch (error) {
            console.error('🚨 알림 설정 로드 실패:', error);
            fetchFailed.value = true;
        }
    };

    return {
        notificationSettings,
        fetchFailed,
        loadNotificationSettings
    };
}
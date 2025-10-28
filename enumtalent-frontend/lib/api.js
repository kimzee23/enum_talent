import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use((config) => {
    if (typeof window !== 'undefined') {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
    }
    return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            if (typeof window !== 'undefined') {
                localStorage.removeItem('authToken');
                localStorage.removeItem('userId');
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export const authAPI = {
    signup: (data) => api.post('/auth/signup', data),
    login: (data) => api.post('/auth/login', data),
    verifyEmail: (token) => api.get(`/auth/verify?token=${token}`),
    logout: (userId) => api.post('/auth/logout', null, { params: { userId } }),
};

export const profileAPI = {
    getProfile: (userId) => api.get(`/profile/talent/me?userId=${userId}`),
    createOrUpdateProfile: (userId, data) => api.post(`/profile/talent/${userId}`, data),
};
export const jobsAPI = {
    getAllJobs: (filters = {}) => api.get('/jobs', { params: filters }),
    getJobById: (jobId) => api.get(`/jobs/${jobId}`),
    getJobsBySkills: (skills) => api.get('/jobs/skills', { params: { skills } }),
    createJob: (userId, data) => api.post('/jobs', data, { params: { userId } }),
    updateJob: (jobId, userId, data) => api.put(`/jobs/${jobId}`, data, { params: { userId } }),
    deleteJob: (jobId, userId) => api.delete(`/jobs/${jobId}`, { params: { userId } }),
};

export default api;
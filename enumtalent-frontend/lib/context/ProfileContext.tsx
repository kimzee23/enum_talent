'use client'

import { createContext, useContext, useState, ReactNode } from 'react'
import {ProfileContextType, ProfileData} from "@/lib/public/profile";


const initialProfileData: ProfileData = {
    firstName: '',
    lastName: '',
    phone: '',
    location: '',
    bio: '',
    headline: '',
    skills: [],
    experienceLevel: '',
    currentPosition: '',
    company: '',
    highestDegree: '',
    institution: '',
    fieldOfStudy: '',
    graduationYear: null,
    transcript: '',
    statementOfPurpose: '',
    resumeUrl: '',
    preferredRoles: [],
    workMode: '',
    salaryExpectation: '',
    locationPreference: ''
}

const ProfileContext = createContext<ProfileContextType | undefined>(undefined)

export function ProfileProvider({ children }: { children: ReactNode }) {
    const [profileData, setProfileData] = useState<ProfileData>(initialProfileData)
    const [currentStep, setCurrentStep] = useState(1)
    const [isSubmitting, setIsSubmitting] = useState(false)

    const updateProfileData = (data: Partial<ProfileData>) => {
        setProfileData(prev => ({ ...prev, ...data }))
    }

    const nextStep = () => {
        setCurrentStep(prev => Math.min(prev + 1, 5))
    }

    const prevStep = () => {
        setCurrentStep(prev => Math.max(prev - 1, 1))
    }

    const submitProfile = async () => {
        setIsSubmitting(true)
        try {
            // API call to save profile
            const response = await fetch('/api/profile', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(profileData),
            })

            if (!response.ok) {
                throw new Error('Failed to save profile')
            }

            const result = await response.json()
            console.log('Profile saved:', result)
            // You can add success notification or redirect here
        } catch (error) {
            console.error('Error saving profile:', error)
            // You can add error notification here
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <ProfileContext.Provider value={{
            profileData,
            updateProfileData,
            currentStep,
            nextStep,
            prevStep,
            isSubmitting,
            submitProfile
        }}>
            {children}
        </ProfileContext.Provider>
    )
}

export function useProfile() {
    const context = useContext(ProfileContext)
    if (context === undefined) {
        throw new Error('useProfile must be used within a ProfileProvider')
    }
    return context
}
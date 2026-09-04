// User types
export interface User {
  id: string
  email: string
  displayName: string
}

// Focus path types
export type FocusPath =
  | 'TOUGH_DAY'
  | 'SUBSTANCE_RECOVERY'
  | 'MENTAL_HEALTH'
  | 'VETERAN_TRANSITION'
  | 'REENTRY_RESTORATION'
  | 'TESTIMONY_VICTORY'

// Chat message types
export interface ChatMessage {
  text: string
  isUser: boolean
  timestamp: number
}

// Victory log types
export interface VictoryLog {
  id: string
  timestamp: number
  type: 'REFLECT' | 'TRIGGER' | 'CBT' | 'TESTIMONY'
  notes: string
  triggerContext?: string
  automaticThought?: string
  identifiedDistortion?: string
  reframedTruth?: string
  scriptureReference?: string
  userId: string
  userPath: FocusPath
  isShared?: boolean
  authorName?: string
}

// PIN-protected private journal. Only encrypted bytes are persisted.
export interface SecureJournalEntry {
  id: string
  timestamp: number
  iv: string
  cipherText: string
}

export interface SecureJournalPayload {
  notes: string
  analysis?: DistortionAnalysis
}

// Freedom goal types
export interface FreedomGoal {
  id: string
  startDate: number
  struggleType: string
  customDeclaration: string
  milestone?: string
  userId: string
}

// Saved chat types
export interface SavedChat {
  id: string
  timestamp: number
  title: string
  messages: ChatMessage[]
  userPath: FocusPath
  userId: string
  isAutoSaved: boolean
}

// Verse of the day
export interface VerseOfTheDay {
  reference: string
  text: string
  reflection: string
}

// Scripture result
export interface ScriptureResult {
  reference: string
  text: string
  explanation: string
}

// Cognitive distortion analysis
export interface DistortionAnalysis {
  distortions: string
  explanation: string
  reframedTruth: string
  scriptureReference: string
}

// Local resource
export interface LocalResource {
  name: string
  type: string
  address: string
  details: string
  contact: string
  directionUrl: string
}

// Support contact
export interface SupportContact {
  id: string
  label: 'PASTOR' | 'SPOUSE' | 'MENTOR' | 'SPONSOR' | 'FRIEND' | 'CUSTOM'
  name: string
  phone?: string
  email?: string
  customLabel?: string
}

// Academy lesson
export interface AcademyLesson {
  id: number
  stepNumber: number
  stepTitle: string
  title: string
  content: string
  reflectionQuestions: string[]
}

// Inspiration quote
export interface InspirationQuote {
  id: string
  text: string
  reference: string
  category: 'OVERCOMING_CRAVINGS' | 'PEACE_ANXIETY' | 'STRENGTH_FAITH' | 'GRACE_FORGIVENESS' | 'IAM_DECLARATIONS'
}

// API usage tracking
export interface ApiUsage {
  date: string
  count: number
}

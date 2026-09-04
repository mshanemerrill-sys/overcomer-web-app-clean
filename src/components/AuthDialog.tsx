import { useState } from 'react'
import { useAppStore } from '../store/useAppStore'
import { supabase, isSupabaseConfigured } from '../lib/supabase'
import { X, Mail, Lock, User, Cloud, CheckCircle, AlertCircle, LogOut } from 'lucide-react'

type AuthMode = 'signIn' | 'signUp' | 'forgotPassword'

export default function AuthDialog({ onClose }: { onClose: () => void }) {
  const { user, setUser } = useAppStore()
  const [mode, setMode] = useState<AuthMode>('signIn')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [displayName, setDisplayName] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  const handleSignIn = async () => {
    if (!isSupabaseConfigured) {
      // Local mock sign in
      const mockUser = {
        id: `mock_${Date.now()}`,
        email,
        displayName: email.split('@')[0]
      }
      setUser(mockUser)
      setSuccess('Signed in successfully!')
      setTimeout(onClose, 1500)
      return
    }

    setIsLoading(true)
    setError(null)
    try {
      const { data, error } = await supabase.auth.signInWithPassword({ email, password })
      if (error) throw error

      setUser({
        id: data.user.id,
        email: data.user.email!,
        displayName: data.user.user_metadata?.display_name || email.split('@')[0]
      })
      setSuccess('Signed in successfully!')
      setTimeout(onClose, 1500)
    } catch (err: any) {
      setError(err.message || 'Failed to sign in')
    } finally {
      setIsLoading(false)
    }
  }

  const handleSignUp = async () => {
    if (!isSupabaseConfigured) {
      const mockUser = {
        id: `mock_${Date.now()}`,
        email,
        displayName
      }
      setUser(mockUser)
      setSuccess('Account created successfully!')
      setTimeout(onClose, 1500)
      return
    }

    setIsLoading(true)
    setError(null)
    try {
      const { data, error } = await supabase.auth.signUp({
        email,
        password,
        options: {
          data: { display_name: displayName }
        }
      })
      if (error) throw error

      setUser({
        id: data.user!.id,
        email: data.user!.email!,
        displayName
      })
      setSuccess('Account created successfully!')
      setTimeout(onClose, 1500)
    } catch (err: any) {
      setError(err.message || 'Failed to create account')
    } finally {
      setIsLoading(false)
    }
  }

  const handleForgotPassword = async () => {
    if (!isSupabaseConfigured) {
      setSuccess('Password reset simulation sent! (Local mode)')
      return
    }

    setIsLoading(true)
    setError(null)
    try {
      const { error } = await supabase.auth.resetPasswordForEmail(email)
      if (error) throw error
      setSuccess('Password reset email sent!')
    } catch (err: any) {
      setError(err.message || 'Failed to send reset email')
    } finally {
      setIsLoading(false)
    }
  }

  const handleSignOut = async () => {
    if (isSupabaseConfigured) {
      await supabase.auth.signOut()
    }
    setUser(null)
    setSuccess('Signed out successfully')
    setTimeout(onClose, 1000)
  }

  if (user) {
    // Profile view
    return (
      <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 animate-fade-in">
        <div className="bg-white rounded-3xl w-full max-w-md shadow-2xl animate-slide-up">
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Cloud className="w-5 h-5 text-primary-500" />
                <h2 className="font-bold text-gray-900">Account</h2>
              </div>
              <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg">
                <X className="w-5 h-5 text-gray-400" />
              </button>
            </div>
          </div>

          <div className="p-6">
            <div className="text-center mb-6">
              <div className="w-20 h-20 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <User className="w-10 h-10 text-primary-500" />
              </div>
              <h3 className="font-bold text-xl text-gray-900">{user.displayName}</h3>
              <p className="text-sm text-gray-500">{user.email}</p>
            </div>

            <div className="bg-gray-50 rounded-xl p-4 mb-6">
              <div className="flex items-center justify-between text-sm">
                <span className="text-gray-600">Storage Mode</span>
                <span className="font-semibold text-primary-500">
                  {isSupabaseConfigured ? 'Cloud Sync' : 'Local Private'}
                </span>
              </div>
            </div>

            <button
              onClick={handleSignOut}
              className="w-full flex items-center justify-center gap-2 bg-red-50 hover:bg-red-100 text-red-600 font-semibold py-3 px-6 rounded-xl transition-colors"
            >
              <LogOut className="w-5 h-5" />
              Sign Out
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 animate-fade-in">
      <div className="bg-white rounded-3xl w-full max-w-md shadow-2xl animate-slide-up">
        {/* Header */}
        <div className="p-6 border-b border-gray-100">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Cloud className="w-5 h-5 text-primary-500" />
              <h2 className="font-bold text-gray-900">
                {mode === 'signIn' ? 'Sign In' : mode === 'signUp' ? 'Create Account' : 'Reset Password'}
              </h2>
            </div>
            <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg">
              <X className="w-5 h-5 text-gray-400" />
            </button>
          </div>
        </div>

        {/* Messages */}
        <div className="px-6 pt-4">
          {!isSupabaseConfigured && (
            <div className="bg-amber-50 border border-amber-100 rounded-xl p-3 mb-4 flex items-start gap-2">
              <AlertCircle className="w-4 h-4 text-amber-500 flex-shrink-0 mt-0.5" />
              <p className="text-xs text-amber-700">
                Running in local mode. Data is stored privately on this device.
              </p>
            </div>
          )}

          {error && (
            <div className="bg-red-50 border border-red-100 rounded-xl p-3 mb-4 flex items-start gap-2">
              <AlertCircle className="w-4 h-4 text-red-500 flex-shrink-0 mt-0.5" />
              <p className="text-sm text-red-700">{error}</p>
            </div>
          )}

          {success && (
            <div className="bg-green-50 border border-green-100 rounded-xl p-3 mb-4 flex items-start gap-2">
              <CheckCircle className="w-4 h-4 text-green-500 flex-shrink-0 mt-0.5" />
              <p className="text-sm text-green-700">{success}</p>
            </div>
          )}
        </div>

        {/* Form */}
        <div className="p-6 space-y-4">
          {mode === 'signUp' && (
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Full Name</label>
              <div className="relative">
                <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  value={displayName}
                  onChange={(e) => setDisplayName(e.target.value)}
                  placeholder="Your name"
                  className="input-field pl-12"
                />
              </div>
            </div>
          )}

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Email</label>
            <div className="relative">
              <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                className="input-field pl-12"
              />
            </div>
          </div>

          {mode !== 'forgotPassword' && (
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Password</label>
              <div className="relative">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="input-field pl-12"
                />
              </div>
            </div>
          )}

          <button
            onClick={() => {
              if (mode === 'signIn') handleSignIn()
              else if (mode === 'signUp') handleSignUp()
              else handleForgotPassword()
            }}
            disabled={isLoading || !email || (mode !== 'forgotPassword' && !password)}
            className="w-full btn-primary disabled:opacity-50"
          >
            {isLoading ? 'Loading...' : mode === 'signIn' ? 'Sign In' : mode === 'signUp' ? 'Create Account' : 'Send Reset Email'}
          </button>

          <div className="text-center space-y-2">
            {mode === 'signIn' && (
              <>
                <button
                  onClick={() => setMode('forgotPassword')}
                  className="text-sm text-primary-500 hover:underline"
                >
                  Forgot password?
                </button>
                <p className="text-sm text-gray-500">
                  New to OverComer?{' '}
                  <button onClick={() => setMode('signUp')} className="text-primary-500 font-semibold hover:underline">
                    Create Account
                  </button>
                </p>
              </>
            )}

            {mode === 'signUp' && (
              <p className="text-sm text-gray-500">
                Already have an account?{' '}
                <button onClick={() => setMode('signIn')} className="text-primary-500 font-semibold hover:underline">
                  Sign In
                </button>
              </p>
            )}

            {mode === 'forgotPassword' && (
              <button onClick={() => setMode('signIn')} className="text-sm text-primary-500 hover:underline">
                Back to Sign In
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

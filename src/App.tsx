import { useState, useEffect } from 'react'
import { supabase } from './lib/supabase'
import { useAppStore } from './store/useAppStore'
import FocusSelectionScreen from './screens/FocusSelectionScreen'
import MainAppScreen from './screens/MainAppScreen'
import AuthDialog from './components/AuthDialog'
import ApiSettingsDialog from './components/ApiSettingsDialog'
import LoadingScreen from './components/LoadingScreen'

function App() {
  const { userPath, setUserPath, initializeFromStorage, isLoading } = useAppStore()
  const [showAuthDialog, setShowAuthDialog] = useState(false)
  const [showApiDialog, setShowApiDialog] = useState(false)
  const [isInitialized, setIsInitialized] = useState(false)

  useEffect(() => {
    initializeFromStorage()

    // Check for existing session
    supabase.auth.getSession().then(({ data: { session } }) => {
      if (session?.user) {
        useAppStore.getState().setUser({
          id: session.user.id,
          email: session.user.email || '',
          displayName: session.user.user_metadata?.display_name || 'OverComer'
        })
      }
      setIsInitialized(true)
    })

    // Listen for auth changes
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      if (session?.user) {
        useAppStore.getState().setUser({
          id: session.user.id,
          email: session.user.email || '',
          displayName: session.user.user_metadata?.display_name || 'OverComer'
        })
      } else {
        useAppStore.getState().setUser(null)
      }
    })

    return () => subscription.unsubscribe()
  }, [initializeFromStorage])

  if (!isInitialized || isLoading) {
    return <LoadingScreen />
  }

  return (
    <div className="min-h-screen bg-background-light">
      {!userPath ? (
        <FocusSelectionScreen onSelect={setUserPath} />
      ) : (
        <MainAppScreen
          onShowAuth={() => setShowAuthDialog(true)}
          onShowApiSettings={() => setShowApiDialog(true)}
        />
      )}

      {showAuthDialog && (
        <AuthDialog onClose={() => setShowAuthDialog(false)} />
      )}

      {showApiDialog && (
        <ApiSettingsDialog onClose={() => setShowApiDialog(false)} />
      )}
    </div>
  )
}

export default App

import { useState } from 'react'
import { X, Key, Eye, EyeOff, Info, CircleCheck as CheckCircle, Circle as XCircle, Loader } from 'lucide-react'
import { useAppStore } from '../store/useAppStore'
import { isCustomKeyActive, testApiConnection } from '../lib/geminiClient'

export default function ApiSettingsDialog({ onClose }: { onClose: () => void }) {
  const { customApiKey, setCustomApiKey } = useAppStore()
  const [keyInput, setKeyInput] = useState(customApiKey || '')
  const [showKey, setShowKey] = useState(false)
  const [testState, setTestState] = useState<'idle' | 'testing' | 'ok' | 'ratelimit' | 'fail'>('idle')
  const [testError, setTestError] = useState('')

  const keyActive = isCustomKeyActive()

  const handleTest = async () => {
    if (!keyInput.trim()) return
    setTestState('testing')
    setTestError('')
    const result = await testApiConnection(keyInput.trim())
    if (result.ok && result.rateLimit) {
      setTestState('ratelimit')
    } else if (result.ok) {
      setTestState('ok')
    } else {
      setTestState('fail')
      setTestError(result.error || 'Connection failed.')
    }
  }

  const handleSave = () => {
    setCustomApiKey(keyInput.trim() || null)
    onClose()
  }

  const handleClear = () => {
    setKeyInput('')
    setCustomApiKey(null)
    setTestState('idle')
    setTestError('')
  }
  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 animate-fade-in">
      <div className="bg-white rounded-3xl w-full max-w-md shadow-2xl animate-slide-up">
        {/* Header */}
        <div className="p-6 border-b border-gray-100">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center">
                <Key className="w-5 h-5 text-primary-500" />
              </div>
              <h2 className="font-bold text-gray-900">AI Settings</h2>
            </div>
            <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg">
              <X className="w-5 h-5 text-gray-400" />
            </button>
          </div>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6">
          {/* Info Card */}
          <div className="bg-secondary-50 border border-secondary-100 rounded-xl p-4">
            <div className="flex items-start gap-3">
              <Info className="w-5 h-5 text-primary-500 flex-shrink-0 mt-0.5" />
              <div className="space-y-3">
                <p className="text-sm font-semibold text-secondary-800">100% Free &amp; Secure</p>
                <ul className="text-xs text-secondary-700 space-y-2">
                  <li>No cost: Google AI Studio API keys are completely free.</li>
                  <li>Private: Your key is stored only on this device — never on any server.</li>
                  <li>Unlimited: Use AI features as much as you need, at zero cost.</li>
                </ul>
              </div>
            </div>
          </div>

          {/* Status */}
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-gray-600">Connection Status:</span>
            <div className="flex items-center gap-2">
              <span className={`w-2 h-2 rounded-full ${keyActive ? 'bg-green-500' : 'bg-gray-300'}`} />
              <span className={`text-sm font-semibold ${keyActive ? 'text-green-600' : 'text-gray-400'}`}>
                {keyActive ? 'Key Active' : 'No Key Set'}
              </span>
            </div>
          </div>

          {/* API Key Input */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Your Free Gemini API Key</label>
            <div className="relative">
              <input
                type={showKey ? 'text' : 'password'}
                value={keyInput}
                onChange={(e) => { setKeyInput(e.target.value); setTestState('idle') }}
                placeholder="Enter your Google AI Studio API key"
                className="input-field pr-12"
              />
              <button
                type="button"
                onClick={() => setShowKey(!showKey)}
                className="absolute right-3 top-1/2 -translate-y-1/2 p-1 hover:bg-gray-100 rounded"
              >
                {showKey ? (
                  <EyeOff className="w-5 h-5 text-gray-400" />
                ) : (
                  <Eye className="w-5 h-5 text-gray-400" />
                )}
              </button>
            </div>
            <p className="text-xs text-gray-400 mt-2">
              Get your free key at{' '}
              <a
                href="https://aistudio.google.com/apikey"
                target="_blank"
                rel="noopener noreferrer"
                className="text-primary-500 hover:underline"
              >
                aistudio.google.com/apikey
              </a>
              {' '}— no credit card required.
            </p>
          </div>

          {/* Test Connection */}
          {keyInput.trim() && (
            <div>
              <button
                onClick={handleTest}
                disabled={testState === 'testing'}
                className="w-full flex items-center justify-center gap-2 border border-primary-200 text-primary-600 hover:bg-primary-50 disabled:opacity-50 font-semibold py-2.5 rounded-xl transition-colors text-sm"
              >
                {testState === 'testing' && <Loader className="w-4 h-4 animate-spin" />}
                {(testState === 'ok' || testState === 'ratelimit') && <CheckCircle className="w-4 h-4 text-green-500" />}
                {testState === 'fail' && <XCircle className="w-4 h-4 text-red-500" />}
                {testState === 'idle' && <Key className="w-4 h-4" />}
                {testState === 'testing' ? 'Testing...'
                  : testState === 'ok' ? 'Key Verified!'
                  : testState === 'ratelimit' ? 'Key is Valid!'
                  : testState === 'fail' ? 'Test Failed'
                  : 'Test Connection'}
              </button>
              {testState === 'ok' && (
                <p className="text-xs text-green-600 font-semibold mt-1.5 text-center">
                  Your key is working. Tap Save Settings to activate it.
                </p>
              )}
              {testState === 'ratelimit' && (
                <div className="mt-1.5 bg-amber-50 border border-amber-100 rounded-xl p-3">
                  <p className="text-xs text-amber-800 font-semibold">Your key is valid and ready to use.</p>
                  <p className="text-xs text-amber-700 mt-1 leading-relaxed">
                    Google is briefly throttling your key — this usually clears within a few minutes. Save your key now and start chatting. It will work.
                  </p>
                </div>
              )}
              {testState === 'fail' && testError && (
                <p className="text-xs text-red-600 mt-1.5 text-center">{testError}</p>
              )}
            </div>
          )}
        </div>

        {/* Actions */}
        <div className="p-6 border-t border-gray-100 flex gap-3">
          {(customApiKey || keyInput) && (
            <button
              onClick={handleClear}
              className="flex-1 flex items-center justify-center gap-2 border border-red-200 text-red-600 hover:bg-red-50 font-semibold py-3 px-6 rounded-xl transition-colors"
            >
              Clear Key
            </button>
          )}
          <button
            onClick={handleSave}
            className="flex-1 btn-primary"
          >
            Save Settings
          </button>
        </div>
      </div>
    </div>
  )
}

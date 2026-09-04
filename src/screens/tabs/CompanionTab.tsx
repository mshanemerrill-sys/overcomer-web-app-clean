import { useState, useEffect, useRef } from 'react'
import { useAppStore } from '../../store/useAppStore'
import { generateSupportResponse, checkDailyLimit, getTodayUsageCount, isCustomKeyActive } from '../../lib/geminiClient'
import {
  Send, Save, Trash2, History, Loader as Loader2, Key, ExternalLink,
  ChevronDown, ChevronUp, Mic, Volume2, X, Radio, MessageCircle
} from 'lucide-react'
import ApiSettingsDialog from '../../components/ApiSettingsDialog'

export default function CompanionTab() {
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [showApiDialog, setShowApiDialog] = useState(false)
  const [showInstructions, setShowInstructions] = useState(false)
  const [showSavedSessions, setShowSavedSessions] = useState(false)
  const [showLiveVoice, setShowLiveVoice] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  const {
    chatMessages,
    addChatMessage,
    clearChatMessages,
    saveCurrentChat,
    userPath,
    pendingCompanionMessage,
    setPendingCompanionMessage,
    customApiKey,
    savedChats,
    loadSavedChat,
    deleteSavedChat,
  } = useAppStore()

  const hasApiKey = Boolean(customApiKey?.trim())

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [chatMessages])

  // Auto-send a message injected by another tab (e.g. "Renew My Mind" from Inspiration)
  useEffect(() => {
    if (!pendingCompanionMessage) return
    const message = pendingCompanionMessage
    setPendingCompanionMessage(null)

    addChatMessage({ text: message, isUser: true, timestamp: Date.now() })
    setIsLoading(true)
    generateSupportResponse(message, chatMessages, userPath || 'SUBSTANCE_RECOVERY')
      .then(response => {
        if (response === 'NO_API_KEY_SETUP') {
          addChatMessage({ text: '__API_KEY_NEEDED__', isUser: false, timestamp: Date.now() })
        } else {
          addChatMessage({ text: response, isUser: false, timestamp: Date.now() })
        }
      })
      .catch(() => {
        addChatMessage({ text: 'I am here with you. Please try again in a moment.', isUser: false, timestamp: Date.now() })
      })
      .finally(() => setIsLoading(false))
  // runs once on mount to consume the pending message
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const handleSend = async () => {
    if (!input.trim() || isLoading) return

    const message = input.trim()
    setInput('')

    addChatMessage({
      text: message,
      isUser: true,
      timestamp: Date.now()
    })

    setIsLoading(true)
    try {
      const response = await generateSupportResponse(
        message,
        chatMessages,
        userPath || 'SUBSTANCE_RECOVERY'
      )

      if (response === 'NO_API_KEY_SETUP') {
        // Don't add a broken message — just show the setup card prominently
        // (the card is always visible when no key is set, so nothing to add)
        addChatMessage({
          text: '__API_KEY_NEEDED__',
          isUser: false,
          timestamp: Date.now()
        })
      } else {
        addChatMessage({
          text: response,
          isUser: false,
          timestamp: Date.now()
        })
      }
    } catch {
      addChatMessage({
        text: 'I am here with you. Please try again in a moment.',
        isUser: false,
        timestamp: Date.now()
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  const dailyLimitReached = checkDailyLimit()
  const dailyCount = getTodayUsageCount()
  const customKeyActive = isCustomKeyActive()

  return (
    <div className="flex flex-col h-full bg-background-light">
      {/* Header */}
      <div className="bg-white border-b border-gray-100 px-4 py-3">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="font-bold text-gray-900">OverComer's Companion</h2>
            <p className="text-xs text-gray-500">
              {customKeyActive
                ? 'Unlimited AI Support'
                : `${30 - dailyCount}/30 requests today`
              }
            </p>
          </div>
          <div className="flex items-center gap-2">
          <button
            onClick={() => setShowApiDialog(true)}
            className={`p-2 rounded-lg transition-colors ${hasApiKey ? 'hover:bg-gray-100 text-gray-500' : 'bg-primary-100 text-primary-600 hover:bg-primary-200'}`}
            title="API Settings"
          >
            <Key className="w-5 h-5" />
          </button>
          <button
            onClick={() => {
              const title = prompt('Save chat as:', `Session ${new Date().toLocaleDateString()}`)
              if (title) saveCurrentChat(title)
            }}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
            title="Save Chat"
          >
            <Save className="w-5 h-5" />
          </button>
          <button
            onClick={clearChatMessages}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
            title="Clear Chat"
          >
            <Trash2 className="w-5 h-5" />
          </button>
          <button
            onClick={() => setShowSavedSessions(value => !value)}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
            title="Saved Chats"
          >
            <History className="w-5 h-5" />
          </button>
          </div>
        </div>
      </div>

      {/* Legal micro-copy */}
      <div className="bg-amber-50 border-b border-amber-100 px-4 py-2">
        <p className="text-xs text-amber-700 leading-relaxed">
          Your AI OverComer's Companion for biblical encouragement. For professional medical or clinical crisis support, please see the <strong>Re-entry</strong> resource tab.
        </p>
      </div>

      {/* Browser equivalent of the Android Gemini Live voice session */}
      <div className="bg-white border-b border-gray-100 p-3">
        <button
          onClick={() => setShowLiveVoice(true)}
          className="w-full rounded-2xl bg-gradient-to-r from-primary-600 to-primary-400 text-white px-4 py-3 flex items-center gap-3 text-left shadow-sm"
        >
          <span className="w-10 h-10 rounded-full bg-white/20 flex items-center justify-center flex-shrink-0">
            <Mic className="w-5 h-5" />
          </span>
          <span className="flex-1">
            <span className="block text-sm font-extrabold">Walk in Victory Hands-Free</span>
            <span className="block text-xs text-white/80 mt-0.5">Talk live and hear your Companion answer aloud.</span>
          </span>
          <Radio className="w-5 h-5 text-white/80" />
        </button>
      </div>

      {/* Daily limit warning */}
      {dailyLimitReached && !customKeyActive && (
        <div className="bg-amber-50 border-b border-amber-100 px-4 py-3">
          <p className="text-sm text-amber-700">
            You've reached today's free limit.{' '}
            <button onClick={() => setShowApiDialog(true)} className="font-semibold underline">
              Add your free API key
            </button>{' '}
            for unlimited access.
          </p>
        </div>
      )}

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">

        {showSavedSessions && (
          <div className="bg-white rounded-2xl border border-primary-100 shadow-sm overflow-hidden">
            <div className="px-4 py-3 bg-primary-50 flex items-center justify-between">
              <div>
                <p className="text-sm font-extrabold text-primary-800">Saved Sessions ({savedChats.length})</p>
                <p className="text-xs text-primary-600">Continue a previous Companion conversation.</p>
              </div>
              <button onClick={() => setShowSavedSessions(false)} className="p-1.5 text-primary-500"><X className="w-4 h-4" /></button>
            </div>
            {savedChats.length === 0 ? (
              <p className="p-4 text-sm text-gray-500 text-center">No sessions saved yet.</p>
            ) : (
              <div className="divide-y divide-gray-100 max-h-64 overflow-y-auto">
                {savedChats.map(chat => (
                  <div key={chat.id} className="flex items-center gap-2 p-3">
                    <button
                      onClick={() => { loadSavedChat(chat); setShowSavedSessions(false) }}
                      className="flex-1 text-left min-w-0"
                    >
                      <p className="text-sm font-bold text-gray-900 truncate">{chat.title}</p>
                      <p className="text-xs text-gray-400">{new Date(chat.timestamp).toLocaleString()} · {chat.messages.length} messages</p>
                    </button>
                    <button
                      onClick={() => { if (window.confirm('Delete this saved Companion session?')) deleteSavedChat(chat.id) }}
                      className="p-2 text-gray-400 hover:text-red-600"
                      aria-label="Delete saved session"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* API Key Setup Card — always shown at top when no key, or after a NO_API_KEY response */}
        {!hasApiKey && (
          <ApiKeySetupCard
            showInstructions={showInstructions}
            onToggleInstructions={() => setShowInstructions(v => !v)}
            onOpenDialog={() => setShowApiDialog(true)}
          />
        )}

        {chatMessages.map((msg, index) => (
          msg.text === '__API_KEY_NEEDED__'
            ? <ApiKeyInlinePrompt key={index} onOpenDialog={() => setShowApiDialog(true)} />
            : <MessageBubble key={index} message={msg} />
        ))}

        {isLoading && (
          <div className="flex justify-start">
            <div className="bg-primary-100 rounded-2xl rounded-bl-md px-4 py-3">
              <Loader2 className="w-5 h-5 animate-spin text-primary-500" />
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="bg-white border-t border-gray-100 p-4 safe-bottom">
        {!hasApiKey && (
          <p className="text-xs text-center text-gray-400 mb-2">
            A free API key is required to chat.{' '}
            <button onClick={() => setShowApiDialog(true)} className="text-primary-500 font-semibold">
              Set up in 2 minutes
            </button>
          </p>
        )}
        <div className="flex items-center gap-2">
          <input
            ref={inputRef}
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={hasApiKey ? 'Talk to your Companion...' : 'Add a free API key to begin...'}
            className="flex-1 input-field"
            disabled={isLoading || (dailyLimitReached && !customKeyActive)}
          />
          <button
            onClick={hasApiKey ? handleSend : () => setShowApiDialog(true)}
            disabled={hasApiKey ? (!input.trim() || isLoading || (dailyLimitReached && !customKeyActive)) : false}
            className="bg-primary-400 hover:bg-primary-500 disabled:bg-gray-200 text-white p-3 rounded-xl transition-colors"
          >
            {hasApiKey ? <Send className="w-5 h-5" /> : <Key className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {showApiDialog && <ApiSettingsDialog onClose={() => setShowApiDialog(false)} />}
      {showLiveVoice && <LiveVoiceSessionDialog onClose={() => setShowLiveVoice(false)} />}
    </div>
  )
}

type VoiceState = 'READY' | 'LISTENING' | 'THINKING' | 'SPEAKING'

interface SpeechRecognitionResultLike {
  results: ArrayLike<{ 0: { transcript: string } }>
}

interface SpeechRecognitionLike {
  continuous: boolean
  interimResults: boolean
  lang: string
  onresult: ((event: SpeechRecognitionResultLike) => void) | null
  onerror: ((event: { error?: string }) => void) | null
  onend: (() => void) | null
  start: () => void
  stop: () => void
  abort: () => void
}

type SpeechRecognitionConstructor = new () => SpeechRecognitionLike

function LiveVoiceSessionDialog({ onClose }: { onClose: () => void }) {
  const { chatMessages, addChatMessage, userPath, customApiKey } = useAppStore()
  const [state, setState] = useState<VoiceState>('READY')
  const [transcript, setTranscript] = useState('')
  const [spokenResponse, setSpokenResponse] = useState('Tap the microphone and speak freely.')
  const [error, setError] = useState('')
  const [voices, setVoices] = useState<SpeechSynthesisVoice[]>([])
  const [voiceName, setVoiceName] = useState('')
  const recognitionRef = useRef<SpeechRecognitionLike | null>(null)

  useEffect(() => {
    const loadVoices = () => {
      const available = window.speechSynthesis?.getVoices() || []
      setVoices(available)
      if (!voiceName && available.length) {
        const preferred = available.find(voice => voice.lang.startsWith('en') && /female|samantha|zira|ava|google us english/i.test(voice.name))
          || available.find(voice => voice.lang.startsWith('en'))
          || available[0]
        setVoiceName(preferred.name)
      }
    }
    loadVoices()
    window.speechSynthesis?.addEventListener('voiceschanged', loadVoices)
    return () => {
      recognitionRef.current?.abort()
      window.speechSynthesis?.cancel()
      window.speechSynthesis?.removeEventListener('voiceschanged', loadVoices)
    }
  }, [voiceName])

  const speak = (text: string) => {
    if (!window.speechSynthesis) {
      setState('READY')
      return
    }
    window.speechSynthesis.cancel()
    const utterance = new SpeechSynthesisUtterance(text.replace(/[*#_`]/g, '').replace(/\s+/g, ' ').trim())
    utterance.rate = 0.95
    utterance.pitch = 1
    utterance.voice = voices.find(voice => voice.name === voiceName) || null
    utterance.onstart = () => setState('SPEAKING')
    utterance.onend = () => setState('READY')
    utterance.onerror = () => setState('READY')
    window.speechSynthesis.speak(utterance)
  }

  const respond = async (text: string) => {
    addChatMessage({ text, isUser: true, timestamp: Date.now() })
    setState('THINKING')
    try {
      const response = await generateSupportResponse(text, chatMessages, userPath || 'SUBSTANCE_RECOVERY')
      if (response === 'NO_API_KEY_SETUP') {
        setError('Add your Gemini API key before starting a live Companion session.')
        setState('READY')
        return
      }
      addChatMessage({ text: response, isUser: false, timestamp: Date.now() })
      setSpokenResponse(response)
      speak(response)
    } catch {
      setError('I could not complete that response. Please try again.')
      setState('READY')
    }
  }

  const listen = () => {
    setError('')
    if (!customApiKey?.trim()) {
      setError('Add your Gemini API key before starting a live Companion session.')
      return
    }
    if (state === 'SPEAKING') {
      window.speechSynthesis.cancel()
      setState('READY')
    }
    const browserWindow = window as typeof window & {
      SpeechRecognition?: SpeechRecognitionConstructor
      webkitSpeechRecognition?: SpeechRecognitionConstructor
    }
    const Recognition = browserWindow.SpeechRecognition || browserWindow.webkitSpeechRecognition
    if (!Recognition) {
      setError('Live speech recognition is not available in this browser. Chrome or Edge usually provides the best support.')
      return
    }
    const recognition = new Recognition()
    recognition.continuous = false
    recognition.interimResults = false
    recognition.lang = 'en-US'
    recognition.onresult = event => {
      const text = event.results[0]?.[0]?.transcript?.trim() || ''
      setTranscript(text)
      if (text) void respond(text)
    }
    recognition.onerror = event => {
      setError(event.error === 'not-allowed' ? 'Microphone permission was denied. Allow microphone access in your browser settings.' : 'I could not hear that clearly. Please tap and try again.')
      setState('READY')
    }
    recognition.onend = () => setState(current => current === 'LISTENING' ? 'READY' : current)
    recognitionRef.current = recognition
    setState('LISTENING')
    recognition.start()
  }

  return (
    <div className="fixed inset-0 z-[70] bg-[#141218]/95 text-white flex items-center justify-center p-4">
      <div className="w-full max-w-lg max-h-[94vh] overflow-y-auto rounded-3xl bg-[#211F26] border border-white/10 shadow-2xl p-5">
        <div className="flex items-center justify-between gap-3">
          <div>
            <p className="text-xs font-bold tracking-[0.18em] text-primary-200">GEMINI LIVE SESSION</p>
            <h3 className="text-xl font-extrabold mt-1">OverComer’s Companion</h3>
          </div>
          <button onClick={onClose} className="p-2 rounded-full bg-white/10 hover:bg-white/20"><X className="w-5 h-5" /></button>
        </div>

        <div className="py-8 flex flex-col items-center text-center">
          <div className={`relative w-36 h-36 rounded-full flex items-center justify-center transition-all ${
            state === 'LISTENING' ? 'bg-teal-500/25 scale-105' : state === 'SPEAKING' ? 'bg-primary-400/25 animate-pulse' : 'bg-white/5'
          }`}>
            <div className={`absolute inset-3 rounded-full border-2 ${state === 'LISTENING' ? 'border-teal-300 animate-ping' : 'border-primary-300/40'}`} />
            {state === 'THINKING' ? <Loader2 className="w-12 h-12 animate-spin text-primary-200" />
              : state === 'SPEAKING' ? <Volume2 className="w-12 h-12 text-primary-200" />
              : <Mic className="w-12 h-12 text-white" />}
          </div>
          <p className="mt-5 text-sm font-extrabold tracking-wide">
            {state === 'LISTENING' ? 'LISTENING… SPEAK FREELY'
              : state === 'THINKING' ? 'COMPANION IS REFLECTING…'
              : state === 'SPEAKING' ? 'COMPANION IS SPEAKING'
              : 'READY WHEN YOU ARE'}
          </p>
          {transcript && <p className="mt-4 text-sm text-white/65">“{transcript}”</p>}
        </div>

        <div className="rounded-2xl bg-white/5 p-4 min-h-24">
          <div className="flex items-center gap-2 mb-2 text-primary-200">
            <MessageCircle className="w-4 h-4" />
            <p className="text-xs font-bold">COMPANION RESPONSE</p>
          </div>
          <p className="text-sm leading-relaxed text-white/85 whitespace-pre-wrap">{spokenResponse}</p>
        </div>

        {voices.length > 0 && (
          <label className="block mt-4 text-xs font-bold text-white/60">
            COMPANION VOICE
            <select value={voiceName} onChange={event => setVoiceName(event.target.value)} className="mt-2 w-full rounded-xl bg-[#2D2A33] border border-white/10 px-3 py-3 text-sm text-white">
              {voices.filter(voice => voice.lang.startsWith('en')).map(voice => <option key={voice.name} value={voice.name}>{voice.name}</option>)}
            </select>
          </label>
        )}

        {error && <p className="mt-4 rounded-xl bg-red-500/15 border border-red-300/30 p-3 text-sm text-red-100">{error}</p>}

        <button
          onClick={listen}
          disabled={state === 'THINKING' || state === 'LISTENING'}
          className="mt-5 w-full rounded-2xl bg-primary-400 hover:bg-primary-300 disabled:opacity-50 py-4 font-extrabold flex items-center justify-center gap-2"
        >
          <Mic className="w-5 h-5" />
          {state === 'SPEAKING' ? 'Interrupt to Speak' : 'Tap to Talk'}
        </button>
        <p className="mt-3 text-[11px] text-center text-white/40">Your browser will ask permission to use the microphone. Voice processing availability depends on the browser and device.</p>
      </div>
    </div>
  )
}

function ApiKeySetupCard({
  showInstructions,
  onToggleInstructions,
  onOpenDialog
}: {
  showInstructions: boolean
  onToggleInstructions: () => void
  onOpenDialog: () => void
}) {
  return (
    <div className="bg-gradient-to-br from-primary-50 to-secondary-50 rounded-2xl border border-primary-100 p-4 shadow-sm">
      <div className="flex items-start gap-3 mb-3">
        <div className="w-10 h-10 bg-primary-500 rounded-xl flex items-center justify-center flex-shrink-0">
          <Key className="w-5 h-5 text-white" />
        </div>
        <div>
          <h3 className="font-bold text-gray-900 text-sm">Unlock Your Free AI Companion</h3>
          <p className="text-xs text-gray-600 mt-0.5">
            A free Google Gemini API key is required. No credit card. Takes 2 minutes.
          </p>
        </div>
      </div>

      <div className="flex gap-2 mb-3">
        <a
          href="https://aistudio.google.com/apikey"
          target="_blank"
          rel="noopener noreferrer"
          className="flex-1 flex items-center justify-center gap-2 bg-white border border-primary-200 text-primary-600 font-semibold text-xs py-2.5 px-3 rounded-xl hover:bg-primary-50 transition-colors"
        >
          <ExternalLink className="w-3.5 h-3.5" />
          Get Free Key
        </a>
        <button
          onClick={onOpenDialog}
          className="flex-1 bg-primary-500 hover:bg-primary-600 text-white font-semibold text-xs py-2.5 px-3 rounded-xl transition-colors"
        >
          Enter Key
        </button>
      </div>

      <button
        onClick={onToggleInstructions}
        className="w-full flex items-center justify-between text-xs text-gray-500 hover:text-gray-700 transition-colors py-1"
      >
        <span className="font-medium">Step-by-step instructions</span>
        {showInstructions ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
      </button>

      {showInstructions && (
        <div className="mt-3 space-y-2.5 border-t border-primary-100 pt-3">
          {[
            { step: '1', text: 'Tap "Get Free Key" above — this opens Google AI Studio in your browser.' },
            { step: '2', text: 'Sign in with any Google account (Gmail, etc.). It\'s free.' },
            { step: '3', text: 'Click "Create API Key" — Google generates your personal key instantly.' },
            { step: '4', text: 'Copy the complete key exactly as Google displays it.' },
            { step: '5', text: 'Tap "Enter Key" above, paste your key, and tap Save. You\'re done!' }
          ].map(({ step, text }) => (
            <div key={step} className="flex items-start gap-2.5">
              <span className="w-5 h-5 bg-primary-500 text-white rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
                {step}
              </span>
              <p className="text-xs text-gray-700 leading-relaxed">{text}</p>
            </div>
          ))}
          <div className="bg-green-50 border border-green-200 rounded-xl p-3 mt-2">
            <p className="text-xs text-green-800 font-medium">
              Your key is stored only in this browser and is sent directly to Google only when the app requests a response. It is not synced to The Faith Connection.
            </p>
          </div>
        </div>
      )}
    </div>
  )
}

function ApiKeyInlinePrompt({ onOpenDialog }: { onOpenDialog: () => void }) {
  return (
    <div className="flex justify-start">
      <div className="max-w-[85%] bg-white shadow-md border border-primary-100 rounded-2xl rounded-bl-md px-4 py-3">
        <p className="text-sm text-gray-800 leading-relaxed">
          To respond, I need a free Google Gemini API key configured on your device. It takes about 2 minutes and is completely free.
        </p>
        <button
          onClick={onOpenDialog}
          className="mt-3 w-full flex items-center justify-center gap-2 bg-primary-500 hover:bg-primary-600 text-white font-semibold text-sm py-2.5 px-4 rounded-xl transition-colors"
        >
          <Key className="w-4 h-4" />
          Set Up Free API Key
        </button>
      </div>
    </div>
  )
}

function MessageBubble({ message }: { message: { text: string; isUser: boolean; timestamp: number } }) {
  const formattedTime = new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

  return (
    <div className={`flex ${message.isUser ? 'justify-end' : 'justify-start'}`}>
      <div className={`max-w-[85%] ${
        message.isUser
          ? 'bg-primary-400 text-white rounded-2xl rounded-br-md'
          : 'bg-white shadow-md border border-gray-100 rounded-2xl rounded-bl-md'
      }`}>
        <div className="px-4 py-3">
          <p className={`text-sm leading-relaxed whitespace-pre-wrap ${
            message.isUser ? 'text-white' : 'text-gray-800'
          }`}>
            {message.text}
          </p>
          <p className={`text-xs mt-1 ${
            message.isUser ? 'text-white/70' : 'text-gray-400'
          }`}>
            {formattedTime}
          </p>
        </div>
      </div>
    </div>
  )
}

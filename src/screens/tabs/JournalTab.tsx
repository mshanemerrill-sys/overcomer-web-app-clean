import { useEffect, useState } from 'react'
import {
  AlertCircle, Brain, Check, Download, Eye, EyeOff, FileText, Heart,
  Loader2, Lock, LockOpen, Plus, ShieldCheck, Trash2, X
} from 'lucide-react'
import { useAppStore } from '../../store/useAppStore'
import { analyzeCognitiveDistortion } from '../../lib/geminiClient'
import type {
  DistortionAnalysis, FocusPath, SecureJournalEntry, SecureJournalPayload, VictoryLog
} from '../../lib/types'

type LogType = 'REFLECT' | 'TRIGGER' | 'CBT' | 'TESTIMONY'
type JournalMode = 'TRACKER' | 'PRIVATE'

interface DecryptedEntry {
  id: string
  timestamp: number
  payload: SecureJournalPayload
}

export default function JournalTab() {
  const [mode, setMode] = useState<JournalMode>('TRACKER')

  return (
    <div className="min-h-full bg-background-light pb-8">
      <div className="sticky top-0 z-20 bg-white border-b border-gray-100 px-4 pt-4 pb-3">
        <div className="flex items-center justify-between gap-3 mb-3">
          <div>
            <h2 className="text-xl font-extrabold text-[#1D1B20]">My Victory Tracker & Journal</h2>
            <p className="text-sm text-[#49454F]">Record growth publicly or write privately behind your PIN.</p>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-2 rounded-2xl bg-[#F3EDF7] p-1.5">
          <ModeButton active={mode === 'TRACKER'} onClick={() => setMode('TRACKER')} icon={<FileText className="w-4 h-4" />}>
            Victory Tracker
          </ModeButton>
          <ModeButton active={mode === 'PRIVATE'} onClick={() => setMode('PRIVATE')} icon={<Lock className="w-4 h-4" />}>
            Private Journal
          </ModeButton>
        </div>
      </div>

      {mode === 'TRACKER' ? <VictoryTracker /> : <PrivateJournal />}
    </div>
  )
}

function ModeButton({ active, onClick, icon, children }: {
  active: boolean
  onClick: () => void
  icon: React.ReactNode
  children: React.ReactNode
}) {
  return (
    <button
      onClick={onClick}
      className={`flex items-center justify-center gap-2 rounded-xl px-3 py-2.5 text-xs font-bold transition-colors ${
        active ? 'bg-primary-500 text-white shadow-sm' : 'text-[#49454F] hover:bg-white/60'
      }`}
    >
      {icon}{children}
    </button>
  )
}

function VictoryTracker() {
  const { victoryLogs, addVictoryLog, deleteVictoryLog, userPath } = useAppStore()
  const [filterType, setFilterType] = useState<'ALL' | LogType>('ALL')
  const [showAddModal, setShowAddModal] = useState(false)

  const filtered = filterType === 'ALL'
    ? victoryLogs
    : victoryLogs.filter(log => log.type === filterType)

  return (
    <div className="relative p-4 space-y-4">
      <div className="flex gap-2 overflow-x-auto pb-1">
        {([
          ['ALL', 'All'], ['REFLECT', 'Reflections'], ['TRIGGER', 'Triggers'],
          ['CBT', 'Thought Reframes'], ['TESTIMONY', 'Testimonies']
        ] as const).map(([value, label]) => (
          <button
            key={value}
            onClick={() => setFilterType(value)}
            className={`whitespace-nowrap rounded-full px-3 py-1.5 text-xs font-semibold border transition-colors ${
              filterType === value
                ? 'bg-primary-500 text-white border-primary-500'
                : 'bg-white text-[#49454F] border-[#CAC4D0]'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {filtered.length === 0 ? (
        <div className="bg-white rounded-3xl border border-[#CAC4D0]/60 p-8 text-center">
          <FileText className="w-11 h-11 mx-auto text-primary-300 mb-3" />
          <h3 className="font-bold text-[#1D1B20]">No logs recorded</h3>
          <p className="text-sm text-[#49454F] mt-1">Tap the + button to log your first step in victory.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map(log => (
            <VictoryLogCard key={log.id} log={log} onDelete={() => {
              if (window.confirm('Delete this tracker entry?')) deleteVictoryLog(log.id)
            }} />
          ))}
        </div>
      )}

      <button
        onClick={() => setShowAddModal(true)}
        className="fixed right-5 bottom-24 z-20 w-14 h-14 rounded-2xl bg-primary-500 hover:bg-primary-600 text-white shadow-xl flex items-center justify-center"
        aria-label="Add a victory tracker entry"
      >
        <Plus className="w-7 h-7" />
      </button>

      {showAddModal && (
        <AddLogModal
          userPath={userPath || 'SUBSTANCE_RECOVERY'}
          onClose={() => setShowAddModal(false)}
          onAdd={log => {
            addVictoryLog(log)
            setShowAddModal(false)
          }}
        />
      )}
    </div>
  )
}

function VictoryLogCard({ log, onDelete }: { log: VictoryLog; onDelete: () => void }) {
  const label = log.type === 'REFLECT' ? 'Reflection'
    : log.type === 'TRIGGER' ? 'Trigger'
    : log.type === 'CBT' ? 'Thought Reframe'
    : 'Testimony & Victory'
  const color = log.type === 'TRIGGER' ? 'text-red-600 bg-red-50'
    : log.type === 'CBT' ? 'text-teal-700 bg-teal-50'
    : log.type === 'TESTIMONY' ? 'text-amber-700 bg-amber-50'
    : 'text-primary-600 bg-primary-50'

  return (
    <article className="bg-white rounded-2xl border border-[#CAC4D0]/60 shadow-sm p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <span className={`inline-flex rounded-full px-2.5 py-1 text-[11px] font-bold ${color}`}>{label}</span>
          <p className="text-xs text-gray-400 mt-2">{new Date(log.timestamp).toLocaleString()}</p>
        </div>
        <button onClick={onDelete} className="p-2 rounded-lg text-gray-400 hover:bg-red-50 hover:text-red-600" aria-label="Delete entry">
          <Trash2 className="w-4 h-4" />
        </button>
      </div>
      <p className="mt-3 text-sm leading-relaxed text-[#1D1B20] whitespace-pre-wrap">{log.notes}</p>
      {log.triggerContext && <Detail label="Trigger context" text={log.triggerContext} />}
      {log.automaticThought && <Detail label="Automatic thought" text={log.automaticThought} />}
      {log.reframedTruth && <Detail label="Reframed truth" text={log.reframedTruth} accent />}
      {log.scriptureReference && <p className="mt-2 text-sm font-semibold text-primary-600">⚓ {log.scriptureReference}</p>}
      {log.isShared && <p className="mt-3 text-xs font-semibold text-amber-700">Shared on your local Testimony & Victory Board</p>}
    </article>
  )
}

function Detail({ label, text, accent = false }: { label: string; text: string; accent?: boolean }) {
  return (
    <div className={`mt-3 rounded-xl p-3 text-sm ${accent ? 'bg-teal-50 text-teal-900' : 'bg-[#F3EDF7] text-[#49454F]'}`}>
      <strong>{label}: </strong>{text}
    </div>
  )
}

function PrivateJournal() {
  const {
    secureJournalPinHash, secureJournalEntries, setSecureJournalPinHash,
    addSecureJournalEntry, deleteSecureJournalEntry, clearSecureJournal
  } = useAppStore()
  const [sessionPin, setSessionPin] = useState<string | null>(null)
  const [pinInput, setPinInput] = useState('')
  const [confirmPin, setConfirmPin] = useState('')
  const [pinError, setPinError] = useState('')
  const [showPin, setShowPin] = useState(false)
  const [notes, setNotes] = useState('')
  const [analysis, setAnalysis] = useState<DistortionAnalysis | undefined>()
  const [isAnalyzing, setIsAnalyzing] = useState(false)
  const [isSaving, setIsSaving] = useState(false)
  const [entries, setEntries] = useState<DecryptedEntry[]>([])

  const isUnlocked = Boolean(sessionPin)

  useEffect(() => {
    if (!sessionPin || !secureJournalPinHash) {
      setEntries([])
      return
    }
    let cancelled = false
    Promise.all(secureJournalEntries.map(entry => decryptEntry(entry, sessionPin, secureJournalPinHash)))
      .then(values => {
        if (!cancelled) setEntries(values.filter((entry): entry is DecryptedEntry => Boolean(entry)))
      })
    return () => { cancelled = true }
  }, [sessionPin, secureJournalEntries, secureJournalPinHash])

  const handlePin = async () => {
    setPinError('')
    if (!/^\d{4}$/.test(pinInput)) {
      setPinError('Enter exactly four numbers.')
      return
    }
    const hash = await hashPin(pinInput)
    if (!secureJournalPinHash) {
      if (pinInput !== confirmPin) {
        setPinError('The two PIN entries do not match.')
        return
      }
      setSecureJournalPinHash(hash)
      setSessionPin(pinInput)
      setPinInput('')
      setConfirmPin('')
      return
    }
    if (hash !== secureJournalPinHash) {
      setPinError('That PIN is incorrect. Please try again.')
      return
    }
    setSessionPin(pinInput)
    setPinInput('')
  }

  const analyze = async () => {
    if (!notes.trim()) return
    setIsAnalyzing(true)
    try {
      setAnalysis(await analyzeCognitiveDistortion(notes.trim()))
    } finally {
      setIsAnalyzing(false)
    }
  }

  const save = async () => {
    if (!sessionPin || !secureJournalPinHash || !notes.trim()) return
    setIsSaving(true)
    try {
      const entry = await encryptEntry({ notes: notes.trim(), analysis }, sessionPin, secureJournalPinHash)
      addSecureJournalEntry(entry)
      setNotes('')
      setAnalysis(undefined)
    } finally {
      setIsSaving(false)
    }
  }

  const exportEntries = () => {
    const body = entries.map(entry => {
      const a = entry.payload.analysis
      return [
        new Date(entry.timestamp).toLocaleString(),
        entry.payload.notes,
        a ? `Patterns noticed: ${a.distortions}\nHow the thought works: ${a.explanation}\nReframed truth: ${a.reframedTruth}\nScripture: ${a.scriptureReference}` : ''
      ].filter(Boolean).join('\n\n')
    }).join('\n\n------------------------------\n\n')
    const url = URL.createObjectURL(new Blob([`OVERCOMER PRIVATE JOURNAL EXPORT\n\n${body}`], { type: 'text/plain' }))
    const link = document.createElement('a')
    link.href = url
    link.download = `OverComer-Private-Journal-${new Date().toISOString().slice(0, 10)}.txt`
    link.click()
    URL.revokeObjectURL(url)
  }

  if (!isUnlocked) {
    const isSetup = !secureJournalPinHash
    return (
      <div className="p-4">
        <div className="bg-white rounded-3xl border border-[#CAC4D0]/60 p-6 shadow-sm text-center">
          <div className="w-16 h-16 mx-auto rounded-full bg-primary-100 text-primary-600 flex items-center justify-center mb-4">
            <Lock className="w-8 h-8" />
          </div>
          <h3 className="text-lg font-extrabold text-[#1D1B20]">{isSetup ? 'Setup Secure PIN Lock' : 'Secure Journal Locked'}</h3>
          <p className="text-sm text-[#49454F] mt-2 leading-relaxed">
            {isSetup
              ? 'Choose a four-digit PIN. Your private entries will be encrypted on this device and cannot be read without it.'
              : 'Enter your four-digit PIN to decrypt and view your private journal.'}
          </p>

          <PinField value={pinInput} onChange={setPinInput} show={showPin} onToggle={() => setShowPin(!showPin)} label={isSetup ? 'Create PIN' : 'Enter PIN'} />
          {isSetup && <PinField value={confirmPin} onChange={setConfirmPin} show={showPin} label="Confirm PIN" />}
          {pinError && <p className="mt-3 text-sm font-semibold text-red-600">{pinError}</p>}

          <button onClick={handlePin} className="mt-4 w-full btn-primary py-3 flex items-center justify-center gap-2">
            {isSetup ? <ShieldCheck className="w-5 h-5" /> : <LockOpen className="w-5 h-5" />}
            {isSetup ? 'Create Secure Journal' : 'Unlock Journal'}
          </button>
          {!isSetup && (
            <button
              onClick={() => {
                if (window.confirm('This permanently erases every encrypted private-journal entry and removes the PIN. Continue?')) clearSecureJournal()
              }}
              className="mt-4 text-xs font-semibold text-red-600 hover:underline"
            >
              Forgot PIN? Reset and erase private journal
            </button>
          )}
          <p className="mt-5 text-[11px] text-gray-400 leading-relaxed">
            Your PIN cannot be recovered. Keep it somewhere safe. Tracker entries are separate and are not affected.
          </p>
        </div>
      </div>
    )
  }

  return (
    <div className="p-4 space-y-4">
      <div className="bg-white rounded-2xl border border-[#CAC4D0]/60 p-4 shadow-sm">
        <div className="flex items-center justify-between gap-3 mb-4">
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-green-600" />
            <div>
              <p className="font-bold text-[#1D1B20] text-sm">Passcode Guard Enabled</p>
              <p className="text-xs text-gray-400">Encrypted only on this device</p>
            </div>
          </div>
          <button onClick={() => { setSessionPin(null); setNotes(''); setAnalysis(undefined) }} className="btn-outline py-2 px-3 text-xs flex items-center gap-1.5">
            <Lock className="w-3.5 h-3.5" /> Lock
          </button>
        </div>

        <label className="block text-xs font-bold text-[#49454F] tracking-wide mb-2">DOCUMENT YOUR RAW THOUGHTS & FEELINGS:</label>
        <textarea
          value={notes}
          onChange={event => { setNotes(event.target.value); setAnalysis(undefined) }}
          rows={6}
          className="input-field resize-none"
          placeholder="This is your private space. Write honestly about what happened, what you felt, and what thoughts followed..."
        />
        <div className="grid grid-cols-2 gap-2 mt-3">
          <button onClick={analyze} disabled={!notes.trim() || isAnalyzing} className="btn-outline py-3 flex items-center justify-center gap-2 disabled:opacity-50">
            {isAnalyzing ? <Loader2 className="w-4 h-4 animate-spin" /> : <Brain className="w-4 h-4" />}
            Renew My Thinking
          </button>
          <button onClick={save} disabled={!notes.trim() || isSaving} className="btn-primary py-3 flex items-center justify-center gap-2 disabled:opacity-50">
            {isSaving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Lock className="w-4 h-4" />}
            Save Privately
          </button>
        </div>
      </div>

      {analysis && <AnalysisCard analysis={analysis} />}

      <div className="flex items-center justify-between gap-3 pt-2">
        <h3 className="font-extrabold text-[#1D1B20]">My Secure Journal Entries</h3>
        <button onClick={exportEntries} disabled={entries.length === 0} className="text-xs font-semibold text-primary-600 flex items-center gap-1.5 disabled:opacity-40">
          <Download className="w-4 h-4" /> Export
        </button>
      </div>

      {entries.length === 0 ? (
        <div className="bg-white rounded-2xl border border-dashed border-[#CAC4D0] p-7 text-center">
          <Lock className="w-9 h-9 mx-auto text-primary-300 mb-2" />
          <p className="font-bold text-[#1D1B20]">Your secure journal is empty</p>
          <p className="text-sm text-[#49454F] mt-1">Write above to build a private timeline of healing and victory.</p>
        </div>
      ) : entries.map(entry => (
        <article key={entry.id} className="bg-white rounded-2xl border border-[#CAC4D0]/60 p-4 shadow-sm">
          <div className="flex items-start justify-between gap-3">
            <p className="text-xs font-semibold text-primary-600">{new Date(entry.timestamp).toLocaleString()}</p>
            <button onClick={() => {
              if (window.confirm('Delete this private journal entry?')) deleteSecureJournalEntry(entry.id)
            }} className="p-2 -mt-2 -mr-2 text-gray-400 hover:text-red-600" aria-label="Delete private entry">
              <Trash2 className="w-4 h-4" />
            </button>
          </div>
          <p className="mt-2 text-sm leading-relaxed text-[#1D1B20] whitespace-pre-wrap">{entry.payload.notes}</p>
          {entry.payload.analysis && <AnalysisCard analysis={entry.payload.analysis} compact />}
        </article>
      ))}

      <p className="text-[11px] text-center text-gray-400 px-4">
        Thought Reframing applies established cognitive-behavioral principles integrated with biblical truth.
      </p>
    </div>
  )
}

function PinField({ value, onChange, show, onToggle, label }: {
  value: string
  onChange: (value: string) => void
  show: boolean
  onToggle?: () => void
  label: string
}) {
  return (
    <div className="mt-4 text-left">
      <label className="block text-xs font-bold text-[#49454F] mb-1.5">{label}</label>
      <div className="relative">
        <input
          type={show ? 'text' : 'password'}
          inputMode="numeric"
          maxLength={4}
          value={value}
          onChange={event => onChange(event.target.value.replace(/\D/g, '').slice(0, 4))}
          className="input-field text-center text-2xl tracking-[0.5em] font-bold pr-12"
          autoComplete="off"
        />
        {onToggle && (
          <button onClick={onToggle} className="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-gray-400" aria-label="Show or hide PIN">
            {show ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
          </button>
        )}
      </div>
    </div>
  )
}

function AnalysisCard({ analysis, compact = false }: { analysis: DistortionAnalysis; compact?: boolean }) {
  return (
    <div className={`${compact ? 'mt-4' : ''} rounded-2xl border border-teal-200 bg-teal-50 p-4 space-y-3`}>
      <div className="flex items-center gap-2 text-teal-900">
        <Brain className="w-5 h-5" />
        <h4 className="font-extrabold text-sm">Cognitive Thought Renewal Analysis</h4>
      </div>
      <p className="text-sm text-teal-950"><strong>Patterns noticed:</strong> {analysis.distortions}</p>
      <p className="text-sm text-teal-950"><strong>How this thought works:</strong> {analysis.explanation}</p>
      <div className="rounded-xl bg-white/80 p-3 text-sm text-teal-950">
        <strong>✝ The truth—reframed:</strong><br />“{analysis.reframedTruth}”
      </div>
      <p className="text-sm font-semibold text-primary-700">⚓ Scripture: {analysis.scriptureReference}</p>
    </div>
  )
}

function AddLogModal({ onClose, onAdd, userPath }: {
  onClose: () => void
  onAdd: (log: Omit<VictoryLog, 'id' | 'timestamp' | 'userId'>) => void
  userPath: FocusPath
}) {
  const [type, setType] = useState<LogType>('REFLECT')
  const [notes, setNotes] = useState('')
  const [triggerContext, setTriggerContext] = useState('')
  const [automaticThought, setAutomaticThought] = useState('')
  const [reframedTruth, setReframedTruth] = useState('')
  const [scriptureReference, setScriptureReference] = useState('')
  const [share, setShare] = useState(type === 'TESTIMONY')
  const user = useAppStore(state => state.user)

  useEffect(() => setShare(type === 'TESTIMONY'), [type])

  const options: Array<{ type: LogType; label: string; icon: React.ReactNode }> = [
    { type: 'REFLECT', label: 'Reflection', icon: <Heart className="w-4 h-4" /> },
    { type: 'TRIGGER', label: 'Trigger', icon: <AlertCircle className="w-4 h-4" /> },
    { type: 'CBT', label: 'Thought Reframe', icon: <Brain className="w-4 h-4" /> },
    { type: 'TESTIMONY', label: 'Testimony', icon: <Check className="w-4 h-4" /> }
  ]

  return (
    <div className="fixed inset-0 bg-black/55 z-50 flex items-end sm:items-center justify-center sm:p-4">
      <div className="bg-white rounded-t-3xl sm:rounded-3xl w-full max-w-lg max-h-[92vh] overflow-y-auto">
        <div className="sticky top-0 bg-white flex items-center justify-between p-5 border-b border-gray-100">
          <h2 className="text-lg font-extrabold text-[#1D1B20]">New Tracker Entry</h2>
          <button onClick={onClose} className="p-2 rounded-lg hover:bg-gray-100"><X className="w-5 h-5" /></button>
        </div>
        <div className="p-5 space-y-4">
          <div className="grid grid-cols-2 gap-2">
            {options.map(option => (
              <button key={option.type} onClick={() => setType(option.type)} className={`flex items-center justify-center gap-2 p-3 rounded-xl text-xs font-bold border-2 ${type === option.type ? 'border-primary-500 bg-primary-50 text-primary-700' : 'border-transparent bg-gray-50 text-gray-600'}`}>
                {option.icon}{option.label}
              </button>
            ))}
          </div>
          <FieldLabel label={type === 'TESTIMONY' ? 'Share your victory story' : 'Notes'}>
            <textarea value={notes} onChange={event => setNotes(event.target.value)} rows={4} className="input-field resize-none" placeholder="Write honestly about this moment..." />
          </FieldLabel>
          {(type === 'TRIGGER' || type === 'CBT') && (
            <FieldLabel label="Trigger context"><input value={triggerContext} onChange={event => setTriggerContext(event.target.value)} className="input-field" placeholder="What happened just before this?" /></FieldLabel>
          )}
          {type === 'CBT' && (
            <>
              <FieldLabel label="Automatic thought"><textarea value={automaticThought} onChange={event => setAutomaticThought(event.target.value)} rows={2} className="input-field resize-none" /></FieldLabel>
              <FieldLabel label="Reframed truth"><textarea value={reframedTruth} onChange={event => setReframedTruth(event.target.value)} rows={2} className="input-field resize-none" /></FieldLabel>
              <FieldLabel label="Scripture reference"><input value={scriptureReference} onChange={event => setScriptureReference(event.target.value)} className="input-field" placeholder="e.g., Philippians 4:13" /></FieldLabel>
            </>
          )}
          {type === 'TESTIMONY' && (
            <label className="flex items-start gap-3 rounded-xl bg-amber-50 p-3 text-sm text-amber-900">
              <input type="checkbox" checked={share} onChange={event => setShare(event.target.checked)} className="mt-1" />
              <span><strong>Add to Testimony & Victory Board</strong><br /><span className="text-xs">This board is stored on this device unless cloud sync is configured.</span></span>
            </label>
          )}
        </div>
        <div className="sticky bottom-0 bg-white p-5 border-t border-gray-100 flex gap-3">
          <button onClick={onClose} className="flex-1 btn-outline">Cancel</button>
          <button
            disabled={!notes.trim()}
            onClick={() => onAdd({
              type, notes: notes.trim(), triggerContext: triggerContext || undefined,
              automaticThought: automaticThought || undefined, identifiedDistortion: undefined,
              reframedTruth: reframedTruth || undefined, scriptureReference: scriptureReference || undefined,
              userPath, isShared: type === 'TESTIMONY' && share,
              authorName: user?.displayName || 'An OverComer'
            })}
            className="flex-1 btn-primary disabled:opacity-50"
          >Save Entry</button>
        </div>
      </div>
    </div>
  )
}

function FieldLabel({ label, children }: { label: string; children: React.ReactNode }) {
  return <label className="block text-sm font-semibold text-[#49454F] space-y-2"><span>{label}</span>{children}</label>
}

async function hashPin(pin: string): Promise<string> {
  const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(`overcomer:${pin}`))
  return Array.from(new Uint8Array(digest)).map(byte => byte.toString(16).padStart(2, '0')).join('')
}

async function deriveKey(pin: string, pinHash: string): Promise<CryptoKey> {
  const keyMaterial = await crypto.subtle.importKey('raw', new TextEncoder().encode(pin), 'PBKDF2', false, ['deriveKey'])
  return crypto.subtle.deriveKey(
    { name: 'PBKDF2', salt: new TextEncoder().encode(pinHash.slice(0, 32)), iterations: 150_000, hash: 'SHA-256' },
    keyMaterial,
    { name: 'AES-GCM', length: 256 },
    false,
    ['encrypt', 'decrypt']
  )
}

async function encryptEntry(payload: SecureJournalPayload, pin: string, pinHash: string): Promise<SecureJournalEntry> {
  const iv = crypto.getRandomValues(new Uint8Array(12))
  const key = await deriveKey(pin, pinHash)
  const cipher = await crypto.subtle.encrypt({ name: 'AES-GCM', iv }, key, new TextEncoder().encode(JSON.stringify(payload)))
  return {
    id: `private_${Date.now()}_${Math.random().toString(36).slice(2, 9)}`,
    timestamp: Date.now(),
    iv: bytesToBase64(iv),
    cipherText: bytesToBase64(new Uint8Array(cipher))
  }
}

async function decryptEntry(entry: SecureJournalEntry, pin: string, pinHash: string): Promise<DecryptedEntry | null> {
  try {
    const key = await deriveKey(pin, pinHash)
    const plain = await crypto.subtle.decrypt(
      { name: 'AES-GCM', iv: base64ToBytes(entry.iv) },
      key,
      base64ToBytes(entry.cipherText)
    )
    return { id: entry.id, timestamp: entry.timestamp, payload: JSON.parse(new TextDecoder().decode(plain)) as SecureJournalPayload }
  } catch {
    return null
  }
}

function bytesToBase64(bytes: Uint8Array): string {
  let binary = ''
  bytes.forEach(byte => { binary += String.fromCharCode(byte) })
  return btoa(binary)
}

function base64ToBytes(value: string): Uint8Array {
  return Uint8Array.from(atob(value), char => char.charCodeAt(0))
}

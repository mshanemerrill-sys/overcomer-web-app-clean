import { useState } from 'react'
import { useAppStore } from '../store/useAppStore'
import { X, Wind, Users, Phone, AlertCircle, Plus, Trash2 } from 'lucide-react'
import type { SupportContact } from '../lib/types'

type SOSMode = 'grounding' | 'contacts' | 'helplines'

export default function SOSOverlay({ onClose }: { onClose: () => void }) {
  const [activeMode, setActiveMode] = useState<SOSMode>('grounding')

  return (
    <div className="fixed inset-0 bg-black/70 z-50 flex items-center justify-center p-4 animate-fade-in">
      <div className="bg-white rounded-3xl w-full max-w-md max-h-[90vh] overflow-hidden shadow-2xl animate-slide-up">
        {/* Header */}
        <div className="bg-gradient-to-r from-red-600 to-red-700 p-6 text-white">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold">Sovereign Shield SOS</h2>
            <button onClick={onClose} className="p-2 rounded-lg hover:bg-white/20 transition-colors">
              <X className="w-5 h-5" />
            </button>
          </div>
          <p className="text-white/80 text-sm mt-2">
            You are not alone. Help is available.
          </p>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-gray-100">
          {[
            { key: 'grounding', label: 'Grounding', icon: <Wind className="w-4 h-4" /> },
            { key: 'contacts', label: 'Inner Circle', icon: <Users className="w-4 h-4" /> },
            { key: 'helplines', label: 'Helplines', icon: <Phone className="w-4 h-4" /> }
          ].map(tab => (
            <button
              key={tab.key}
              onClick={() => setActiveMode(tab.key as SOSMode)}
              className={`flex-1 flex items-center justify-center gap-1.5 py-3 font-medium text-sm transition-colors ${
                activeMode === tab.key
                  ? 'text-primary-500 border-b-2 border-primary-500'
                  : 'text-gray-400 hover:text-gray-600'
              }`}
            >
              {tab.icon}
              {tab.label}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="overflow-y-auto p-6 max-h-[60vh]">
          {activeMode === 'grounding' && <GroundingContent />}
          {activeMode === 'contacts' && <ContactsContent />}
          {activeMode === 'helplines' && <HelplinesContent />}
        </div>
      </div>
    </div>
  )
}

function GroundingContent() {
  const [breathingActive, setBreathingActive] = useState(false)
  const [breathingPhase, setBreathingPhase] = useState<'inhale' | 'hold' | 'exhale'>('inhale')

  return (
    <div className="space-y-6">
      {/* Breathing Exercise */}
      <div className="text-center">
        <h3 className="font-bold text-gray-900 mb-4">Paced Breathing</h3>

        <div className="relative w-48 h-48 mx-auto">
          {/* Background circle */}
          <div className="absolute inset-0 bg-primary-100 rounded-full" />

          {/* Animated breathing circle */}
          <div
            className={`absolute inset-4 bg-primary-400 rounded-full flex items-center justify-center transition-all duration-[4000ms] ${
              breathingActive
                ? breathingPhase === 'inhale'
                  ? 'scale-100'
                  : breathingPhase === 'hold'
                  ? 'scale-100'
                  : 'scale-75'
                : 'scale-75'
            }`}
          >
            <div className="text-white text-center">
              <p className="text-2xl font-bold">
                {breathingPhase === 'inhale' ? 'Inhale' : breathingPhase === 'hold' ? 'Hold' : 'Exhale'}
              </p>
              <p className="text-sm opacity-80">
                {breathingPhase === 'inhale' ? '4 sec' : breathingPhase === 'hold' ? '4 sec' : '6 sec'}
              </p>
            </div>
          </div>
        </div>

        <button
          onClick={() => {
            if (!breathingActive) {
              setBreathingActive(true)
              setBreathingPhase('inhale')

              setTimeout(() => setBreathingPhase('hold'), 4000)
              setTimeout(() => setBreathingPhase('exhale'), 8000)
              setTimeout(() => {
                setBreathingPhase('inhale')
                setBreathingActive(false)
              }, 14000)
            }
          }}
          disabled={breathingActive}
          className="mt-4 btn-primary disabled:opacity-50"
        >
          {breathingActive ? 'Breathing...' : 'Start Breathing Exercise'}
        </button>
      </div>

      {/* 5-4-3-2-1 Grounding */}
      <div>
        <h3 className="font-bold text-gray-900 mb-4">5-4-3-2-1 Grounding Method</h3>
        <div className="space-y-3">
          <GroundingStep number={5} sense="Things you can SEE" color="bg-primary-100 text-primary-600" />
          <GroundingStep number={4} sense="Things you can TOUCH" color="bg-accent-teal/10 text-accent-teal" />
          <GroundingStep number={3} sense="Things you can HEAR" color="bg-accent-amber/10 text-accent-coral" />
          <GroundingStep number={2} sense="Things you can SMELL" color="bg-primary-100 text-primary-600" />
          <GroundingStep number={1} sense="Thing you can TASTE" color="bg-accent-teal/10 text-accent-teal" />
        </div>
      </div>
    </div>
  )
}

function GroundingStep({
  number,
  sense,
  color
}: {
  number: number
  sense: string
  color: string
}) {
  const items = Array(number).fill(null)

  return (
    <div className={`${color} rounded-xl p-3`}>
      <p className="font-semibold text-sm mb-2">{number} {sense}:</p>
      <div className="flex gap-2">
        {items.map((_, i) => (
          <div
            key={i}
            className="flex-1 h-8 bg-white rounded-lg border-2 border-dashed border-current opacity-40"
          />
        ))}
      </div>
    </div>
  )
}

function ContactsContent() {
  const { supportContacts, addSupportContact, deleteSupportContact } = useAppStore()
  const [showAddModal, setShowAddModal] = useState(false)
  const [newContact, setNewContact] = useState({
    label: 'FRIEND' as SupportContact['label'],
    name: '',
    phone: '',
    email: '',
    customLabel: ''
  })

  const contactLabels: SupportContact['label'][] = ['PASTOR', 'SPOUSE', 'MENTOR', 'SPONSOR', 'FRIEND', 'CUSTOM']

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-bold text-gray-900">Support Contacts</h3>
        <button
          onClick={() => setShowAddModal(true)}
          className="p-2 bg-primary-100 text-primary-600 rounded-xl hover:bg-primary-200 transition-colors"
        >
          <Plus className="w-5 h-5" />
        </button>
      </div>

      {supportContacts.length === 0 ? (
        <div className="text-center py-8 bg-gray-50 rounded-xl">
          <Users className="w-10 h-10 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500 text-sm">Add your inner circle contacts</p>
        </div>
      ) : (
        <div className="space-y-3">
          {supportContacts.map(contact => (
            <div
              key={contact.id}
              className="bg-white border border-gray-100 rounded-xl p-4 shadow-sm"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-semibold text-gray-900">{contact.name}</p>
                  <p className="text-xs text-gray-400">
                    {contact.label === 'CUSTOM' ? contact.customLabel : contact.label}
                  </p>
                </div>
                <div className="flex gap-2">
                  {contact.phone && (
                    <a
                      href={`tel:${contact.phone}`}
                      className="p-2 bg-accent-teal/10 text-accent-teal rounded-lg hover:bg-accent-teal/20"
                    >
                      <Phone className="w-4 h-4" />
                    </a>
                  )}
                  <button
                    onClick={() => deleteSupportContact(contact.id)}
                    className="p-2 hover:bg-red-50 text-gray-400 hover:text-red-500 rounded-lg"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add Contact Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/50 z-60 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl w-full max-w-md animate-slide-up">
            <div className="p-4 border-b border-gray-100">
              <h3 className="font-bold text-gray-900">Add Support Contact</h3>
            </div>
            <div className="p-4 space-y-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Relationship</label>
                <div className="grid grid-cols-3 gap-2">
                  {contactLabels.map(label => (
                    <button
                      key={label}
                      onClick={() => setNewContact({ ...newContact, label })}
                      className={`py-2 px-3 rounded-lg text-sm font-medium transition-colors ${
                        newContact.label === label
                          ? 'bg-primary-400 text-white'
                          : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                      }`}
                    >
                      {label.charAt(0) + label.slice(1).toLowerCase()}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Name</label>
                <input
                  type="text"
                  value={newContact.name}
                  onChange={(e) => setNewContact({ ...newContact, name: e.target.value })}
                  className="input-field"
                  placeholder="Contact name"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Phone</label>
                <input
                  type="tel"
                  value={newContact.phone}
                  onChange={(e) => setNewContact({ ...newContact, phone: e.target.value })}
                  className="input-field"
                  placeholder="(555) 123-4567"
                />
              </div>
            </div>
            <div className="p-4 border-t border-gray-100 flex gap-3">
              <button onClick={() => setShowAddModal(false)} className="flex-1 btn-outline">Cancel</button>
              <button
                onClick={() => {
                  if (newContact.name) {
                    addSupportContact(newContact as any)
                    setShowAddModal(false)
                    setNewContact({ label: 'FRIEND', name: '', phone: '', email: '', customLabel: '' })
                  }
                }}
                className="flex-1 btn-primary"
              >
                Add Contact
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function HelplinesContent() {
  const helplines = [
    {
      name: '988 Suicide & Crisis Lifeline',
      number: '988',
      description: '24/7 free, confidential support for people in distress',
      urgent: true
    },
    {
      name: 'SAMHSA National Helpline',
      number: '1-800-662-4357',
      description: 'Free, confidential treatment referral for substance use',
      urgent: true
    },
    {
      name: 'Crisis Text Line',
      number: 'Text HOME to 741741',
      description: 'Free crisis counseling via text message',
      urgent: true
    },
    {
      name: 'Emergency Services',
      number: '911',
      description: 'For immediate danger or medical emergencies',
      urgent: true
    }
  ]

  return (
    <div>
      <h3 className="font-bold text-gray-900 mb-4">Emergency Helplines</h3>

      <div className="bg-red-50 border border-red-100 rounded-xl p-4 mb-4">
        <p className="text-sm text-red-700 flex items-center gap-2">
          <AlertCircle className="w-4 h-4 flex-shrink-0" />
          If you are in immediate danger, experiencing a medical emergency, or having thoughts of self-harm, please call emergency services or go to your nearest emergency room.
        </p>
      </div>

      <div className="space-y-3">
        {helplines.map((line, index) => (
          <div
            key={index}
            className={`rounded-xl p-4 border ${
              line.urgent
                ? 'bg-red-50 border-red-100'
                : 'bg-white border-gray-100'
            }`}
          >
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <h4 className="font-semibold text-gray-900">{line.name}</h4>
                <p className="text-sm text-gray-600 mt-1">{line.description}</p>
              </div>
              {line.number !== 'Text HOME to 741741' && (
                <a
                  href={`tel:${line.number}`}
                  className="p-3 bg-accent-coral text-white rounded-xl hover:bg-red-600 transition-colors"
                >
                  <Phone className="w-5 h-5" />
                </a>
              )}
            </div>
            <p className="mt-2 font-bold text-primary-500">{line.number}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

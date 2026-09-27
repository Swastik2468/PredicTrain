import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const EXAMPLE_TRAIN_NUMBERS = ['12901', '12951', '12002', '12627', '12841', '12259'];

/**
 * Train number search form.
 * Validates the train number format and navigates to /train/{trainNumber}
 * using React Router without reloading the page.
 */
export const TrainSearch: React.FC = () => {
  const [trainNumber, setTrainNumber] = useState<string>('');
  const [validationError, setValidationError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleTrackTrain = (rawValue: string) => {
    const trimmed = rawValue.trim();
    if (!trimmed) {
      setValidationError('Please enter a train number.');
      return;
    }
    if (!/^\d{5}$/.test(trimmed)) {
      setValidationError('Train number must be a 5-digit number (for example, 12951).');
      return;
    }

    setValidationError(null);
    navigate(`/train/${trimmed}`);
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    handleTrackTrain(trainNumber);
  };

  const handleExampleClick = (exampleNumber: string) => {
    setTrainNumber(exampleNumber);
    setValidationError(null);
    navigate(`/train/${exampleNumber}`);
  };

  return (
    <div className="train-search-wrapper">
      <form onSubmit={handleSubmit} className="train-search-form" noValidate>
        <label htmlFor="train-number-input" className="search-label">
          Enter Train Number
        </label>
        <div className="search-input-row">
          <input
            id="train-number-input"
            type="text"
            inputMode="numeric"
            value={trainNumber}
            onChange={(e) => {
              setTrainNumber(e.target.value);
              if (validationError) {
                setValidationError(null);
              }
            }}
            placeholder="12951"
            className={`search-input ${validationError ? 'search-input-error' : ''}`}
            aria-label="Enter Train Number"
            autoFocus
          />
          <button type="submit" className="primary-button">
            Track Train
          </button>
        </div>

        {validationError && (
          <p className="field-error" role="alert">
            {validationError}
          </p>
        )}
      </form>

      <div className="search-examples">
        <span className="examples-label">Try:</span>
        <div className="examples-list">
          {EXAMPLE_TRAIN_NUMBERS.map((num, idx) => (
            <React.Fragment key={num}>
              <button
                type="button"
                className="example-chip"
                onClick={() => handleExampleClick(num)}
              >
                {num}
              </button>
              {idx < EXAMPLE_TRAIN_NUMBERS.length - 1 && (
                <span className="example-comma">, </span>
              )}
            </React.Fragment>
          ))}
        </div>
      </div>
    </div>
  );
};
